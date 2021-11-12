package com.cordova;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CordovaDemoClass {

    private AppiumDriver driver;
    private static AppiumDriverLocalService service;
    private String deviceName;

    @BeforeClass
    public static void startAppiumServer() {
        AppiumServiceBuilder builder = new AppiumServiceBuilder();

        builder.usingAnyFreePort();
        builder.usingDriverExecutable(new File("C:\\Program Files\\nodejs\\node.exe"));
        builder.withAppiumJS(new File("C:\\Users\\vagha\\AppData\\Roaming\\npm\\node_modules\\appium"));

        service = AppiumDriverLocalService.buildService(builder);
        service.start();
    }

    @BeforeMethod
    public void startSession() {
        try {
            Process process = Runtime.getRuntime().exec("adb devices");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;

            Pattern pattern = Pattern.compile("^([a-zA-Z0-9\\-]+)(\\s+)(device)");
            Matcher matcher;

            while ((line = in.readLine()) != null) {
                if(line.matches(pattern.pattern())) {
                    matcher = pattern.matcher(line);
                    if(matcher.find()){
                        deviceName = matcher.group(1);
                    }
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
        }

        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability("platformName", "Android");
        dc.setCapability("platformVersion", "9");
        dc.setCapability("deviceName", deviceName);
        dc.setCapability("app",  "C:\\Users\\vagha\\IdeaProjects\\Cordova_Tutorial_App_Demo\\resources\\cordova-app-debug.apk");
        dc.setCapability("appPackage", "com.noknok.cordovatutorialapp");
        dc.setCapability("appActivity", "com.noknok.cordovatutorialapp.MainActivity");

        driver = new AndroidDriver<MobileElement>(service.getUrl(), dc);
    }

    @AfterMethod
    public void endSession() {
        try{
            driver.quit();
        }catch (Exception ign) {}
    }


    @AfterClass
    public void stopAppiumServer() {
        service.stop();
    }

    @Test
    public void signToCordovaApp() {
        String usernameXPath = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/android.view.View[3]/android.view.View[1]/android.view.View/android.widget.EditText";
        String passwordXPath = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/android.view.View[3]/android.view.View[1]/android.view.View[3]/android.widget.EditText";
        String nextButtonXpath = "//*[@text='Next']";
        String signinButtonXpath = "//*[@text='Sign In With Password']";
        String headerSettingsXpath = "/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.webkit.WebView/android.webkit.WebView/android.view.View[2]/android.view.View[2]";

        MobileElement usernameField = (MobileElement) driver.findElement(By.xpath(usernameXPath));
        usernameField.sendKeys("appium_user");

        MobileElement nextButton = (MobileElement) driver.findElement(By.xpath(nextButtonXpath));
        nextButton.click();

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        MobileElement passwordField = (MobileElement) driver.findElementByXPath(passwordXPath);
        passwordField.sendKeys("noknok");

        MobileElement signinButton = (MobileElement) driver.findElement(By.xpath(signinButtonXpath));
        signinButton.click();

        MobileElement HeadText = (MobileElement) driver.findElement(By.xpath(headerSettingsXpath));

        String txtSettings = HeadText.getText();

        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        Assert.assertEquals(txtSettings, "Settings");
    }
}
