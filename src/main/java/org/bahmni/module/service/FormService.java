package org.bahmni.module.service;

import java.util.Map;

public interface FormService {

    Map<String, String> getAllLatestFormPaths();

    Map<String, Integer> getFormNamesWithLatestVersionNumber();

    String getFormPath(String formName);

}
