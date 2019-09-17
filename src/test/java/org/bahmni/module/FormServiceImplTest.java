package org.bahmni.module;

import org.bahmni.module.service.FormService;
import org.bahmni.module.service.impl.FormServiceImpl;
import org.bahmni.module.utils.ResourceUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.bahmni.module.service.impl.CommonTestHelper.setValuesForMemberFields;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ResourceUtils.class)
public class FormServiceImplTest {
    FormService formService;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private Resource form2FormListResource;
    private String sql;

    @Before
    public void setUp() throws Exception {
        mockStatic(ResourceUtils.class);
        sql = "form list sql";
        when(ResourceUtils.convertResourceOutputToString(any(Resource.class))).thenReturn(sql);
        formService = new FormServiceImpl();
        setValuesForMemberFields(formService, "openmrsDbTemplate", jdbcTemplate);
        setValuesForMemberFields(formService, "form2FormListResource", form2FormListResource);
    }

    @Test
    public void shouldReturnMapWithKeysAsFormNamesAndValuesAsLatestVersion() {

        String formNameAndVersionSql = "SELECT name , MAX(version) as version FROM form GROUP BY name";
        Map<String, Object> formRow = new LinkedHashMap<>();
        formRow.put("name", "Vitals");
        formRow.put("version", "3");
        List<Map<String, Object>> formRows = new ArrayList<>();
        formRows.add(formRow);
        when(jdbcTemplate.queryForList(formNameAndVersionSql)).thenReturn(formRows);

        Map<String, Integer> formNameAndVersionMap = formService.getFormNamesWithLatestVersionNumber();

        assertEquals(3, formNameAndVersionMap.get("Vitals").intValue());
    }

    @Test
    public void shouldReturnEmptyMapWhenNoFormsAvailable() {

        when(jdbcTemplate.queryForList(sql)).thenReturn(new ArrayList<>());
        Map<String, Integer> formNameAndVersionMap = formService.getFormNamesWithLatestVersionNumber();

        assertEquals(0, formNameAndVersionMap.size());
    }

    @Test
    public void shouldReturnAllFormsAndTheirLocations() {
        final String qeuryForFormNamesAndPaths = "sql to find form names and their paths";
        when(ResourceUtils.convertResourceOutputToString(form2FormListResource))
                .thenReturn(qeuryForFormNamesAndPaths);
        final Map<String, Object> record = new HashMap<>();
        record.put("name", "Vitals");
        record.put("value_reference", "/home/bahmni/clinical_forms/Vitals_1.json");

        when(jdbcTemplate.queryForList(qeuryForFormNamesAndPaths)).thenReturn(singletonList(record));

        final Map<String, String> allLatestFormPaths = formService.getAllLatestFormPaths();

        assertEquals(1, allLatestFormPaths.size());
        assertEquals("/home/bahmni/clinical_forms/Vitals_1.json", allLatestFormPaths.get("Vitals"));
    }
}
