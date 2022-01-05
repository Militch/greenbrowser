package tech.xfs.xfschainexplorer.jsonrpc;

import tech.xfs.xfschainexplorer.common.jsonrpci.Client;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.util.Assert;
import org.web3j.utils.Strings;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HTTPClient implements Client {
    private static final OkHttpClient okc = new OkHttpClient.Builder().build();
    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.get("application/json; charset=utf-8");
    private static final String JSON_RPC_VERSION = "2.0";
    private static final Gson g = new Gson().newBuilder().create();
    private final String apiurl;
    private static final ModelMapper mp = new ModelMapper();
    static {
        mp.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
    public HTTPClient(String apiurl){
        this.apiurl = apiurl;
    }

    @Override
    public <T, D> T call(String method, D data, Class<T> tclass) throws Exception {
        String body = request(method, data);
        Assert.isTrue(!Strings.isEmpty(body), "response.body.string is empty");
        ResponseResult<Map<String, Object>> r = g.fromJson(body,
                new TypeToken<ResponseResult<Map<String, Object>>>() {}.getType());
        Map<String, Object> map = r.getResult();
        return g.fromJson(g.toJson(map), tclass);
    }
    private <D> String request(String method, D data) throws Exception{
        RequestParams<D> params = new RequestParams<>();
        params.setId(1);
        params.setJsonrpc(JSON_RPC_VERSION);
        params.setMethod(method);
        params.setParams(data);
        String jsonParams = g.toJson(params);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, jsonParams);
        Request request = new Request.Builder()
                .url(apiurl)
                .post(body)
                .build();
        try (Response response = okc.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            ResponseBody rb = response.body();
            Assert.notNull(rb, "response.body == null");
            return rb.string();
        }
    }
    @Override
    public <T, D> List<T> callList(String method, D data) throws Exception {
        String body = request(method, data);
        Assert.isTrue(!Strings.isEmpty(body), "response.body.string is empty");
        ResponseResult<List<String>> r = g.fromJson(body,
                new TypeToken<ResponseResult<List<String>>>() {}.getType());
        List<String> list = r.getResult();
        return g.fromJson(g.toJson(list),  new TypeToken<List<T>>() {}.getType());
    }
}
