package tdotapp.com.okhttpdemo.okhttpsUtils;

import android.telecom.Call;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhy on 15/12/15.
 */
public class RequestCall
{
    private com.zhy.http.okhttp.request.OkHttpRequest okHttpRequest;
    private Request request;
    private Call call;

    private long readTimeOut;
    private long writeTimeOut;
    private long connTimeOut;

    private OkHttpClient clone;


    public RequestCall(com.zhy.http.okhttp.request.OkHttpRequest request)
    {
        this.okHttpRequest = request;
    }

    public RequestCall readTimeOut(long readTimeOut)
    {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut)
    {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut)
    {
        this.connTimeOut = connTimeOut;
        return this;
    }


    public Call generateCall(Callback callback)
    {
        request = generateRequest(callback);

        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0)
        {
            readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;

            clone = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .build();

            call = clone.newCall(request);
        } else
        {
            call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
        }
        return call;
    }

    private Request generateRequest(Callback callback)
    {
        return okHttpRequest.generateRequest(callback);
    }

    public void execute(Callback callback)
    {
        generateCall(callback);

        if (callback != null)
        {
            callback.onBefore(request);
        }

        OkHttpUtils.getInstance().execute(this, callback);
    }

    public Call getCall()
    {
        return call;
    }

    public Request getRequest()
    {
        return request;
    }

    public com.zhy.http.okhttp.request.OkHttpRequest getOkHttpRequest()
    {
        return okHttpRequest;
    }

    public Response execute() throws IOException
    {
        generateCall(null);
        return call.execute();
    }

    public void cancel()
    {
        if (call != null)
        {
            call.cancel();
        }
    }


}