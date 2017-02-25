package cn.net.cc.weibo.http.okhttp;
/**
 * @author  "https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java"
 * @see <a href="https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java">OkHttp sample</a>
 */
public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
