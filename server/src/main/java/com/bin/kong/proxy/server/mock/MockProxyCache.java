package com.bin.kong.proxy.server.mock;

import com.bin.kong.proxy.core.cache.impl.LocalCacheUtils;
import com.bin.kong.proxy.dao.mapper.mock.MockProxyMapper;
import com.bin.kong.proxy.model.mock.entity.MockProxy;
import com.bin.kong.proxy.model.mock.search.MockProxySearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MockProxyCache {
    @Resource
    private MockProxyMapper MockProxyMapper;

    /**
     * 初始化加载 所有Mock 配置
     */
    public void init() {
        try {
            List<MockProxy> MockProxySearchList = MockProxyMapper.searchList(MockProxySearch.builder()
                    .build());

            for (MockProxy MockProxy : MockProxySearchList) {
                LocalCacheUtils.putIfAbsent(getCacheKey(MockProxy.getUrl(), MockProxy.getProxy_port()), MockProxy, 100000, TimeUnit.DAYS);
            }
        } catch (Exception e) {
            log.error("初始化加载Mock配置异常");
        }
    }

    /**
     * 根据url和port生成 缓存key
     *
     * @param url
     * @param port
     * @return
     */
    private String getCacheKey(String url, Integer port) {
        return url + "__" + port;
    }

    /**
     * 添加缓存
     *
     * @param url
     * @param port
     * @param MockProxy
     */
    public void put(String url, Integer port, MockProxy MockProxy) {
        LocalCacheUtils.put(getCacheKey(url, port), MockProxy, 100000, TimeUnit.DAYS);
    }

    /**
     * 删除缓存
     *
     * @param url
     * @param port
     */
    public void remove(String url, Integer port) {
        LocalCacheUtils.remove(getCacheKey(url, port));
    }

    /**
     * 获取配置
     *
     * @param url
     * @param port
     * @return
     */
    public MockProxy get(String url, Integer port) {
        return LocalCacheUtils.get(getCacheKey(url, port));
    }
}
