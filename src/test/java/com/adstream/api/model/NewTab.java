package com.adstream.api.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Faiyyaz.Shaik on 10/14/2016.
 */
public class NewTab extends BodyBuilder{

    private String name;
    @JsonProperty("public")
    private boolean isPublic;
    @JsonProperty("default")
    private boolean isDefault;
    private String tabType;
    private String businessUnitId;
    private String tabId;
 //   String[] myTabId ;

 /*   public NewTab setTabIdFromArray(String[] myTabId){
        for(int i=0; i<myTabId.length; i++){
            String element = myTabId[i];

        }
        return this;
    }

    public String[] getTabIdFromArray(){
        return myTabId;
    }*/

    public NewTab setTabId(String tabId){
        this.tabId = tabId;
        return this;
    }

    public String getTabId(){
        return tabId;
    }

    public NewTab setName(String name){
        this.name = name;
        return this;
    }

    public String getName(){
        return name;
    }

    public NewTab setPublic(boolean isPublic){
        this.isPublic = isPublic;
        return this;
    }

    public boolean getIsPublic(){

        return isPublic;
    }

    public NewTab setDefault(boolean isDefault){
        this.isDefault = isDefault;
        return this;
    }

    public NewTab setTabType(String tabType){
        this.tabType = tabType;
        return this;
    }

    public String getTabType(){

        return tabType;
    }

    public NewTab setBusinessUnitId(String businessUnitId){
        this.businessUnitId = businessUnitId;
        return this;
    }

    public String getBusinessUnitId(){

        return businessUnitId;
    }
}
