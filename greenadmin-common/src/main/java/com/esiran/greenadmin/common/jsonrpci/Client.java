package com.esiran.greenadmin.common.jsonrpci;

import java.util.List;

public interface Client {
    <T,D> T call(String method, D data, Class<T> tclass) throws Exception;
    <T,D> List<T> callList(String method, D data) throws Exception;

}
