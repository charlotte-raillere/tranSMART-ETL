/*******************************************************************************
 * Copyright (c) 2012 Sanofi-Aventis Recherche et D�veloppement.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *    Sanofi-Aventis Recherche et D�veloppement - initial API and implementation
 ******************************************************************************/
package fr.sanofi.fcl4transmart.controllers.listeners.proteomicsData;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.widgets.Listener;
import org.pentaho.di.job.Job;

import fr.sanofi.fcl4transmart.controllers.LoadAnnotationListener;
import fr.sanofi.fcl4transmart.handlers.PreferencesHandler;
import fr.sanofi.fcl4transmart.handlers.etlPreferences;
import fr.sanofi.fcl4transmart.model.classes.workUI.proteomicsData.ProteomicsLoadAnnotationUI;
import fr.sanofi.fcl4transmart.model.interfaces.DataTypeItf;

/**
 *This class controls the platform annotation loading 
 */	
public class ProteomicsLoadAnnotationListener extends LoadAnnotationListener implements Listener{
	public ProteomicsLoadAnnotationListener(ProteomicsLoadAnnotationUI ui, DataTypeItf dataType){
		super(ui, dataType);
	}
	@Override
	protected void setParameters(){
		newPath=dataType.getPath().getAbsolutePath()+File.separator+platformId+"_annotation.txt";
		storedProcedureLaunched=".*Starting entry \\[run_I2B2_LOAD_PROTEOMICS_ANNOT_DEAPP\\].*";;
		storedProcedreEnded=".*Finished job entry \\[run_I2B2_LOAD_PROTEOMICS_ANNOT_DEAPP\\].*";
		queryStoredProcedureStarted="select max(JOB_ID) from CZ_JOB_AUDIT where STEP_DESC='Starting I2B2_LOAD_PROTEOMICS_ANNOTTATION'";
		queryStoredProcedureEnded="select * from cz_job_audit where (step_desc like '%End i2b2_load_proteomics_annottation%' or step_status='FAIL') and job_id=";
		unixDir="proteomics";
		checkPs=".*load_proteomics_annotation.*";
	}
	@Override
	protected String getJobPath() throws Exception {
		URL jobUrl = new URL("platform:/plugin/fr.sanofi.fcl4transmart/jobs_kettle/load_proteomics_annotation.kjb");
		jobUrl = FileLocator.toFileURL(jobUrl);  
		String jobPath = jobUrl.getPath();
		
		jobUrl = new URL("platform:/plugin/fr.sanofi.fcl4transmart/jobs_kettle/load_proteomics_annotation_to_de_gpl_info.ktr");
		jobUrl = FileLocator.toFileURL(jobUrl); 
		jobUrl = new URL("platform:/plugin/fr.sanofi.fcl4transmart/jobs_kettle/load_proteomics_annotation_to_lt.ktr");
		jobUrl = FileLocator.toFileURL(jobUrl); 
		jobUrl = new URL("platform:/plugin/fr.sanofi.fcl4transmart/jobs_kettle/run_i2b2_load_proteomics_annot_deapp.ktr");
		jobUrl = FileLocator.toFileURL(jobUrl); 
		jobUrl = new URL("platform:/plugin/fr.sanofi.fcl4transmart/jobs_kettle/cz_end_audit.ktr");
		jobUrl = FileLocator.toFileURL(jobUrl); 
		jobUrl = new URL("platform:/plugin/fr.sanofi.fcl4transmart/jobs_kettle/cz_start_audit.ktr");
		jobUrl = FileLocator.toFileURL(jobUrl); 
		jobUrl = new URL("platform:/plugin/fr.sanofi.fcl4transmart/jobs_kettle/cz_write_audit.ktr");
		jobUrl = FileLocator.toFileURL(jobUrl);		
		return jobPath;
	}
	@Override
	protected void setJobMetadata(Job job) throws Exception {
		job.getJobMeta().setParameterValue("DATA_LOCATION", dataType.getPath().getAbsolutePath());
		File sort=new File(sortName);
		if(!sort.exists()){
			FileUtils.forceMkdir(sort);
		}
		job.getJobMeta().setParameterValue("SORT_DIR", sort.getAbsolutePath());		
		job.getJobMeta().setParameterValue("GPL_ID", platformId);
		job.getJobMeta().setParameterValue("ANNOTATION_TITLE", annotationTitle);			
		job.getJobMeta().setParameterValue("LOAD_TYPE", "I");
		job.getJobMeta().setParameterValue("TM_CZ_DB_SERVER", PreferencesHandler.getDbServer());
		job.getJobMeta().setParameterValue("TM_CZ_DB_NAME", PreferencesHandler.getDbName());
		job.getJobMeta().setParameterValue("TM_CZ_DB_PORT", PreferencesHandler.getDbPort());
		job.getJobMeta().setParameterValue("TM_CZ_DB_USER", PreferencesHandler.getTm_czUser());
		job.getJobMeta().setParameterValue("TM_CZ_DB_PWD", PreferencesHandler.getTm_czPwd());
		job.getJobMeta().setParameterValue("TM_LZ_DB_SERVER",PreferencesHandler.getDbServer());
		job.getJobMeta().setParameterValue("TM_LZ_DB_NAME", PreferencesHandler.getDbName());
		job.getJobMeta().setParameterValue("TM_LZ_DB_PORT", PreferencesHandler.getDbPort());
		job.getJobMeta().setParameterValue("TM_LZ_DB_USER", PreferencesHandler.getTm_lzUser());
		job.getJobMeta().setParameterValue("TM_LZ_DB_PWD", PreferencesHandler.getTm_lzPwd());
		job.getJobMeta().setParameterValue("DEAPP_DB_SERVER", PreferencesHandler.getDbServer());
		job.getJobMeta().setParameterValue("DEAPP_DB_NAME", PreferencesHandler.getDbName());
		job.getJobMeta().setParameterValue("DEAPP_DB_PORT", PreferencesHandler.getDbPort());
		job.getJobMeta().setParameterValue("DEAPP_DB_USER", PreferencesHandler.getDeappUser());
		job.getJobMeta().setParameterValue("DEAPP_DB_PWD", PreferencesHandler.getDeappPwd());	
	}
	@Override
	protected String createUnixCommand() throws Exception {
		String fileLoc=c.pwd();
		String command=etlPreferences.getKettleDirectory()+"/kitchen.sh -norep=Y ";
		command+="-file="+etlPreferences.getJobsDirectory()+"/load_proteomics_annotation.kjb ";
		command+="-param:DATA_LOCATION="+fileLoc+"  ";
		String sortPath="";
		try{
			c.cd(etlPreferences.getFilesDirectory()+"/.sort");
			sortPath=etlPreferences.getFilesDirectory()+"/.sort";
		}catch(Exception e){
			try{
				c.cd(etlPreferences.getFilesDirectory());
				c.mkdir(".sort");
				sortPath=etlPreferences.getFilesDirectory()+"/.sort";
			}catch(Exception e2){
				e2.printStackTrace();
				loadAnnotationUI.displayMessage("Error when creating sort directory");
				return null;
			}
		}
		command+="-param:SORT_DIR="+sortPath+" ";
		command+="-param:GPL_ID="+platformId+"  ";
		command+="-param:ANNOTATION_TITLE="+annotationTitle+"  ";
		command+="-param:LOAD_TYPE=I ";
		return command;
	}
}
