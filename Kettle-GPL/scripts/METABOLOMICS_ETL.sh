#! /bin/bash
./data-integration/kitchen.sh -norep=Y -file=Kettle-ETL/Req9_Metabolomics_Data/load_metabolomic_data.kjb -log=load_metabolomic_data.log -param:DATA_LOCATION=exam2 -param:STUDY_ID=GSE37427 -param:MAP_FILENAME=metabolomics_sub_sam_data.txt -param:DATA_TYPE='R' -param:SORT_DIR=/tmp -param:TOP_NODE='\Public Studies\ExampleStudy\Test02\' -param:COLUMN_MAPPING_FILE=metabolomics_column_mapping.txt -param:LOAD_TYPE=I -param:DATA_FILE_PREFIX=dmetabolomics_sample
