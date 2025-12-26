-- 滑动窗口限流 Lua 脚本
-- KEYS[1]: 限流 key
-- ARGV[1]: 时间窗口（秒）
-- ARGV[2]: 最大请求数
-- ARGV[3]: 当前时间戳（秒）

local key = KEYS[1]
local window = tonumber(ARGV[1])
local limit = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local clearBefore = now - window

-- 清理过期数据
redis.call('ZREMRANGEBYSCORE', key, 0, clearBefore)

-- 获取当前窗口内的请求数
local current = redis.call('ZCARD', key)

if current < limit then
    -- 允许请求，记录本次请求
    redis.call('ZADD', key, now, now .. ':' .. math.random())
    redis.call('EXPIRE', key, window)
    return 1
else
    -- 超过限制
    return 0
end

