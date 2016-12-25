package com.ohrimenko1988.QA_homework_5;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Created by Igor on 22.12.2016.
 */
public class PageObjects
{



    PageObjects(WebDriver driver )
    {

        PageFactory.initElements(driver,this);


    }



    @FindBy(xpath = "//*[@class='hp_sw_logo hpcLogoWhite' or @id='bLogoExp'] ")
    WebElement logoBing;

    @FindBy(xpath = "//*[@name='q']")
    WebElement searchField;

    @FindBy(xpath = "//*[@class='b_searchboxSubmit' or @id='sbBtn'] ")
    WebElement searchButton;

    @FindBy(xpath = "//ol[@id='b_results']/li[1]//h2/a[1]")
    WebElement titleRezultSerch;

    public WebElement fullExpression(WebDriver driver , String part,String full)
    {
        return driver.findElement(By.xpath("//*[@class='sa_tm' and text() = '"+full+"'"+
        " or text()='"+part+"']"));
    }




}
