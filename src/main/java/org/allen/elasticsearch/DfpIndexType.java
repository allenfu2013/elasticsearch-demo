package org.allen.elasticsearch;

/**
 * 索引类型
 */
public enum DfpIndexType {

    DFP_IOS("dfp_ios", "dfp_ios_mapping.json"),
    DFP_ANDROID("dfp_android", "dfp_android_mapping.json");

    private String type;
    private String mappingFile;

    private DfpIndexType(String type, String mappingFile) {
        this.type = type;
        this.mappingFile = mappingFile;
    }

    public String getType() {
        return type;
    }

    public String getMappingFile() {
        return mappingFile;
    }
}
