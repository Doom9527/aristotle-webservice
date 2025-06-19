/*
 * Copyright 2024 Paion Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paiondata.aristotle.common.util;

import com.paiondata.aristotle.common.base.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true")
public class RedisCacheUtil {

    /**
     * Indicates whether caching is enabled or disabled.
     */
    @Value("${spring.redis.enabled}")
    private boolean cacheEnabled;

    @Autowired
    public RedisTemplate redisTemplate;

    public <T> void setCacheObject(final String key, final T value) {
        if (!cacheEnabled) {
            return;
        }

        redisTemplate.opsForValue().set(key, value);
    }

    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        if (!cacheEnabled) {
            return;
        }

        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public boolean expire(final String key, final long timeout) {
        if (!cacheEnabled) {
            return false;
        }

        return expire(key, timeout, TimeUnit.SECONDS);
    }

    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        if (!cacheEnabled) {
            return false;
        }

        return redisTemplate.expire(key, timeout, unit);
    }

    public <T> T getCacheObject(final String key) {
        if (!cacheEnabled) {
            return null;
        }

        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    public void deleteObject(final String uuid) {
        if (!cacheEnabled) {
            return;
        }

        Collection<String> keys = redisTemplate.keys(uuid + "_*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public long deleteObject(final Collection collection) {
        if (!cacheEnabled) {
            return 0;
        }

        return redisTemplate.delete(collection);
    }

    public <T> long setCacheList(final String key, final List<T> dataList) {
        if (!cacheEnabled) {
            return 0;
        }

        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    public <T> List<T> getCacheList(final String key) {
        if (!cacheEnabled) {
            return null;
        }

        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        if (!cacheEnabled) {
            return null;
        }

        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext())
        {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    public <T> Set<T> getCacheSet(final String key) {
        if (!cacheEnabled) {
            return null;
        }

        return redisTemplate.opsForSet().members(key);
    }

    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (!cacheEnabled) {
            return;
        }

        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    public <T> Map<String, T> getCacheMap(final String key) {
        if (!cacheEnabled) {
            return null;
        }

        return redisTemplate.opsForHash().entries(key);
    }

    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        if (!cacheEnabled) {
            return;
        }

        redisTemplate.opsForHash().put(key, hKey, value);
    }

    public <T> T getCacheMapValue(final String key, final String hKey) {
        if (!cacheEnabled) {
            return null;
        }

        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    public void delCacheMapValue(final String key, final String hkey) {
        if (!cacheEnabled) {
            return;
        }

        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, hkey);
    }

    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        if (!cacheEnabled) {
            return null;
        }

        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    public Collection<String> keys(final String pattern) {
        if (!cacheEnabled) {
            return null;
        }

        return redisTemplate.keys(pattern);
    }


    public long setCacheLong(final String key, final Long[] dataList1) {
        if (!cacheEnabled) {
            return 0;
        }

        List<Long> dataList = Stream.of(dataList1).collect(Collectors.toList());
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    public Long[] getCacheLong(final String key) {
        if (!cacheEnabled) {
            return null;
        }

        List<Long> range = redisTemplate.opsForList().range(key, 0, -1);
        Long[] array = range.stream().toArray(Long[]::new);
        return array;
    }
}
