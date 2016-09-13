package org.allen.elasticsearch;

/**
 *
 */
public class ElasticsearchException extends RuntimeException {

    public ElasticsearchException(String message) {
        super(message);
    }

    public ElasticsearchException(String message, Throwable e) {
        super(message, e);
    }
}
