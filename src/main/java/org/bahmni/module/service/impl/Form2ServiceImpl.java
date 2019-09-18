package org.bahmni.module.service.impl;

import org.bahmni.module.service.Form2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.bahmni.module.utils.ResourceUtils.convertResourceOutputToString;

@Component
public class Form2ServiceImpl implements Form2Service {

    private static final String FORM_NAME = "name";
    @Qualifier("openmrsJdbcTemplate")
    @Autowired
    JdbcTemplate openmrsDbTemplate;
    @Value("classpath:sql/form2FormList.sql")
    private Resource form2FormListResource;

    private Map<String, String> allLatestFormPaths;
    private Map<String, Integer> formNameToLatestVersionMap;


    public Map<String, String> getAllLatestFormPaths() {
        Map<String, String> formPaths = new HashMap<>();
        List<Map<String, Object>> forms = executeFormListQuery();
        for (Map<String, Object> form : forms) {
            String name = (String) form.get(FORM_NAME);
            String valueReference = (String) form.get("value_reference");
            formPaths.put(name, valueReference);
        }
        return formPaths;
    }

    private List<Map<String, Object>> executeFormListQuery() {
        final String form2FormListQuery = convertResourceOutputToString(form2FormListResource);
        return openmrsDbTemplate.queryForList(form2FormListQuery);
    }

    public Map<String, Integer> getFormNamesWithLatestVersionNumber() {
        LinkedHashMap<String, Integer> formNameAndVersionMap = new LinkedHashMap<>();
        List<Map<String, Object>> forms = getLatestFormNamesWithVersion();
        forms.forEach(form -> {
            String name = (String) form.get(FORM_NAME);
            int version = Integer.parseInt((String) form.get("version"));
            formNameAndVersionMap.put(name, version);
        });
        return formNameAndVersionMap;
    }

    public int getFormLatestVersion(String formName) {
        if (formNameToLatestVersionMap == null) {
            formNameToLatestVersionMap = getFormNamesWithLatestVersionNumber();
        }
        return formNameToLatestVersionMap.get(formName);
    }

    @Override
    public String getFormPath(String formName) {
        if (allLatestFormPaths == null) {
            allLatestFormPaths = getAllLatestFormPaths();
        }
        return allLatestFormPaths.get(formName);
    }

    private List<Map<String, Object>> getLatestFormNamesWithVersion() {
        return openmrsDbTemplate.queryForList("SELECT name , MAX(version) as version FROM form GROUP BY name");
    }
}
