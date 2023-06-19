package com.holland.graph_robot.json_rpc2;

@SuppressWarnings("SpellCheckingInspection")
public class Request {
    public final String jsonrpc = "2.0";
    public final String id;
    public final String method;
    public final Object params;

    public Request(String jsonrpc, String id, String method, Object params) {
        this.id = id;
        this.method = method;
        this.params = null != params ? params : new Object[0];
    }

}
