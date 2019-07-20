package com.example.a13703.my_app.Inter_kou;

/**
 * Created by 13703 on 2019/7/13.
 */

public interface OnDownloadListener {
    /**
     * 下载成功
     */
    void onDownloadSuccess();

    /**
     * @param progress
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载失败
     */
    void onDownloadFailed();

    /**
     * 文件已存在
     */
    void onDownloadAlready();
}
