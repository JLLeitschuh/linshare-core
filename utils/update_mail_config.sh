#!/bin/bash
set -e

g_import_src=../src/main/resources/sql/postgresql/import-postgresql.sql
g_import_new=../src/main/resources/sql/postgresql/import-postgresql.sql.new
g_host=127.0.0.1
g_port=5432
g_database=linshare
g_user=linshare
g_pg_dump=/usr/lib/postgresql/9.4/bin/pg_dump
g_pg_dump=/usr/bin/pg_dump
g_step="$@"
g_output=mails.sql
g_output_clean=mails.clean.sql

if [ -f update_mail_config.cfg ] ; then
    source update_mail_config.cfg
fi

echo "############ Config #########"
echo "host : $g_host"
echo "port : $g_port"
echo "database : $g_database"
echo "user : $g_user"
echo "pg_dump : $g_pg_dump"
echo "#############################"


function dump_and_clean ()
{
    echo dump and clean database : $g_host : $g_port : $g_database  : $g_user
    ${g_pg_dump} -h $g_host -p $g_port -U ${g_user} -t mail_layout -t mail_footer -t mail_content -t mail_config -t mail_content_lang -t mail_footer_lang  -a --inserts --attribute-inserts  $g_database -f ${g_output}
    grep -v "^-- " ${g_output} | grep -v "^--$" | grep -v "^SET" | sed -e '/^$/ d'>> ${g_output_clean}
echo "UPDATE domain_abstract SET mailconfig_id = 1;
UPDATE mail_footer SET readonly = true;
UPDATE mail_layout SET readonly = true;
UPDATE mail_content SET readonly = true;
UPDATE mail_config SET readonly = true;
UPDATE mail_content_lang SET readonly = true;
UPDATE mail_footer_lang SET readonly = true;
" >> ${g_output_clean}
#UPDATE mail_activation SET enable = false;

    sed -i -r -e "s/'2017-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{3,6}'/now()/g" ${g_output_clean}

    echo generated files : ${g_output} ${g_output_clean}
}

function update_embedded ()
{
    echo update embedded sql file :
    # We can not update this file without hibernate4
    # cp -v ${g_output_clean} ../src/main/resources/sql/h2/import-mails.sql
    l_output="../src/test/resources/import-mails-hibernate3.sql"

    echo "
UPDATE domain_abstract SET mailconfig_id = null;
DELETE FROM mail_content_lang ;
DELETE FROM mail_footer_lang ;
DELETE FROM mail_config ;
DELETE FROM mail_content;
DELETE FROM mail_footer;
DELETE FROM mail_layout ;
" > $l_output
    cat ${g_output_clean} >> ${l_output}
    # it seems to be ok with it. we do not replace it anymore
    #sed -i -r -e "s/''Open Sans''/Open Sans/g" ${l_output}
    sed -i -r -e "s/\\\''''/ /g" ${l_output}
#    sed -i -r -e "s/''/ /g" ${l_output}
    echo "Embedded file updated : ${l_output}"

}

function update_postgresql ()
{
    sed -r -e '/-- ###BEGIN-PART-1###/,/###END-PART-1###/ !d' ${g_import_src} > ${g_import_new}
    echo "-- ###BEGIN-PART-2###" >> ${g_import_new}
    cat ${g_output_clean} >> ${g_import_new}
    echo "-- ###END-PART-2###" >> ${g_import_new}
    sed -r -e '/-- ###BEGIN-PART-3###/,/###END-PART-3###/ !d' ${g_import_src} >> ${g_import_new}
    echo update postgresql sql file :
    mv -v ${g_import_new} ${g_import_src}

}



####### MAIN
rm -fv ${g_output}  ${g_output_clean}

if [ "${g_step}" == "help" ] ; then
    echo
    echo "available functions :"
    echo "---------------------"
    declare -F | cut -d' ' -f 3-
    echo
    exit 0
fi

if [ "${g_step}" == "dump" ] ; then
    dump_and_clean
fi

if [ -z "${g_step}" ] ; then
    dump_and_clean
    update_postgresql
    update_embedded
else
    for func in ${g_step}
    do
        $func
    done
fi
