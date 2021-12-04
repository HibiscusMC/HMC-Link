package de.skyslycer.hmclink.plugin.caching.user

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class UserCache : ConcurrentHashMap<UUID, UserCacheEntry>()