package org.bahmni.module.service;

import org.bahmni.module.exception.InvalidFormException;
import org.bahmni.module.service.impl.Form2ReaderServiceImpl;
import org.bahmni.module.service.impl.FormFieldPathServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormFieldPathServiceImplTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Mock
    private Form2Service form2Service;
    private FormFieldPathService formFieldPathService;

    @Before
    public void setUp() {
        initMocks(this);
        formFieldPathService = new FormFieldPathServiceImpl(form2Service, new Form2ReaderServiceImpl());
    }

    @Test
    public void shouldReturnFormFieldOfHeightObsControl() {

        when(form2Service.getFormPath("Vitals")).thenReturn("src/test/resources/Vitals_1.json");
        when(form2Service.getFormLatestVersion("Vitals")).thenReturn(1);
        final String formFieldPath = formFieldPathService.getFormFieldPath(asList("Vitals", "Height"));

        assertEquals("Vitals.1/1-0", formFieldPath);
    }

    @Test
    public void shouldThrowInvalidFormExceptionIfInvalidFormNameIsGiven() {

        when(form2Service.getFormPath("Vitals")).thenReturn(null);
        exception.expect(InvalidFormException.class);
        exception.expectMessage("Vitals not found");

        formFieldPathService.getFormFieldPath(asList("Vitals", "Height"));
    }

    @Test
    public void shouldVerifyFormFieldPathsInTheGivenForm() {

        when(form2Service.getFormPath("ComplexForm")).thenReturn("src/test/resources/ComplexForm_1.json");
        when(form2Service.getFormLatestVersion("ComplexForm")).thenReturn(1);

        final String sectionFormFieldPath = formFieldPathService.getFormFieldPath(asList("ComplexForm", "Section"));
        assertEquals("ComplexForm.1/1-0", sectionFormFieldPath);

        final String dateFormFieldPath = formFieldPathService.getFormFieldPath(asList("ComplexForm", "Section", "Date"));
        assertEquals("ComplexForm.1/8-0", dateFormFieldPath);

        final String bmiDataFormFieldPath = formFieldPathService.getFormFieldPath(asList("ComplexForm", "BMI Data"));
        assertEquals("ComplexForm.1/4-0", bmiDataFormFieldPath);

        final String bmiFormFieldPath = formFieldPathService.getFormFieldPath(asList("ComplexForm", "BMI Data", "BMI"));
        assertEquals("ComplexForm.1/5-0", bmiFormFieldPath);

        final String bmiAbnormalFormFieldPath = formFieldPathService.getFormFieldPath(asList("ComplexForm", "BMI Data", "BMI ABNORMAL"));
        assertEquals("ComplexForm.1/6-0", bmiAbnormalFormFieldPath);
    }
}
