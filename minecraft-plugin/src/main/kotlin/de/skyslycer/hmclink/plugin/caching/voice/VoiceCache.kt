package de.skyslycer.hmclink.plugin.caching.voice

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class VoiceCache : ConcurrentHashMap<UUID, VoiceCacheEntry>()