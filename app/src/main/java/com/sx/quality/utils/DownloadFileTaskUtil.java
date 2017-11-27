package com.sx.quality.utils;

import com.sx.quality.listener.DownloadProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class DownloadFileTaskUtil {

    /**
     * 文件下载
     *
     * @param downloadPath
     * @param fileSavePath
     * @param progressListener
     * @return
     * @throws Exception
     */
    public static String downloadFileDoGet(String downloadPath, String fileSavePath, DownloadProgressListener progressListener) throws Exception {
        URL url = new URL(downloadPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 如果是https 添加安全证书
        if (conn instanceof HttpsURLConnection) {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            ((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
            ((HttpsURLConnection) conn).setHostnameVerifier(new TrustAnyHostnameVerifier());
        }

        conn.setRequestMethod("GET"); // 提交模式
        //conn.setDoOutput(false); // 是否输入参数
        conn.setConnectTimeout(10000); // 连接超时 单位毫秒
        conn.setReadTimeout(5000); // 读取超时 单位毫秒
        conn.setRequestProperty("Accept-Charset", "utf-8");
        conn.setRequestProperty("contentType", "utf-8");
        conn.setRequestProperty("Content-Type", "application/octet-stream");

        conn.connect();
        // 检测是否正常返回数据请求 详情参照http协议
        if (conn.getResponseCode() == 200) {
            // 获得输入流
            InputStream is = conn.getInputStream();

            // 新建一个file文件
            File file = new File(fileSavePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            // 对应文件建立输出流
            FileOutputStream fos = new FileOutputStream(file);

            // 新建缓存 用来存储 从网络读取数据 再写入文件
            byte[] buffer = new byte[1024];
            int len = 0;
            // 当没有读到最后的时候
            while ((len = is.read(buffer)) != -1) {
                // 将缓存中的存储的文件流秀娥问file文件
                fos.write(buffer, 0, len);
                progressListener.downloadSize(file.length());
            }
            // 将缓存中的写入file
            fos.flush();
            fos.close();
            // 将输入流 输出流关闭
            is.close();

            return fileSavePath;
        }
        return null;
    }

    /**
     * HTTPS 添加安全证书
     *
     * @author JiangHaiJun
     * @date 2016-2-22
     */
    private static class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    /**
     * HTTPS 添加安全证书
     *
     * @author JiangHaiJun
     * @date 2016-2-22
     */
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
