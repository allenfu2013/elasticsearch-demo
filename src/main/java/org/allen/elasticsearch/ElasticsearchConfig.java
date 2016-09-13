package org.allen.elasticsearch;

/**
 * elasticsearch配置参数
 */
public class ElasticsearchConfig {
    private String clusterName;
    private String hosts;
    private String index;
    private boolean updateMapping = false;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public boolean getUpdateMapping() {
        return updateMapping;
    }

    public void setUpdateMapping(boolean updateMapping) {
        this.updateMapping = updateMapping;
    }

    @Override
    public String toString() {
        return "ElasticsearchConfig{" +
                "clusterName='" + clusterName + '\'' +
                ", hosts='" + hosts + '\'' +
                ", index='" + index + '\'' +
                ", updateMapping=" + updateMapping +
                '}';
    }
}
