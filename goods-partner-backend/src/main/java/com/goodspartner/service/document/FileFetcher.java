package com.goodspartner.service.document;

public interface FileFetcher {
    String updateUrl(String url);
    byte[] getFileThroughInternet(String url) throws Exception;
}