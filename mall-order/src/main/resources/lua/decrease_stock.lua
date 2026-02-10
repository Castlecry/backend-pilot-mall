local productId = KEYS[1]
local stockKey = 'product:stock:' .. productId

local currentStock = redis.call('GET', stockKey)

if currentStock == false then
    return -1
end

currentStock = tonumber(currentStock)

if currentStock <= 0 then
    return 0
end

redis.call('DECRBY', stockKey, 1)

return 1
