
package com.alberovalley.appyuda;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dHVSSU9XeWtKSzd0SEJuWnRqSjBocFE6MQ" ,  /*mailTo = "issues.alberovalley@gmail.com",*/
customReportContent = {  ReportField.REPORT_ID, ReportField.APP_VERSION_CODE   ,ReportField.APP_VERSION_NAME, 
		ReportField.PACKAGE_NAME,  ReportField.FILE_PATH, 
		ReportField.PHONE_MODEL, ReportField.BRAND, ReportField.PRODUCT, ReportField.ANDROID_VERSION, 
		ReportField.BUILD, ReportField.TOTAL_MEM_SIZE, ReportField.AVAILABLE_MEM_SIZE, ReportField.CUSTOM_DATA,
		ReportField.IS_SILENT,  ReportField.STACK_TRACE, ReportField.INITIAL_CONFIGURATION, 
		ReportField.CRASH_CONFIGURATION, ReportField.LOGCAT },
logcatArguments = { "-t", "100", "-v", "long","test:I" ,"*:D","*:S"},        
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text)

public class MyApplication extends Application {
	@Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate(); 
    }

}
