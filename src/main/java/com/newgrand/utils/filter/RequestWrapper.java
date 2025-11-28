package com.newgrand.utils.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class RequestWrapper extends HttpServletRequestWrapper {

    /**
     * 请求体
     */
    private String mBody;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 将body数据存储起来
        mBody = getBody(request);
    }

    /**
     * 获取请求体
     *
     * @param request 请求
     * @return 请求体
     */
    private String getBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = null;
        try {
            StringBuilder buffer = new StringBuilder("");
            request.setCharacterEncoding("UTF-8");
            reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * 获取请求体
     *
     * @return 请求体
     */
    public String getBody() {
        return mBody;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 创建字节数组输入流
        final ByteArrayInputStream bais = new ByteArrayInputStream(mBody.getBytes("UTF-8"));

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }
}
