package com.panomc.plugins.faq

import com.panomc.platform.api.PanoPlugin
import com.panomc.platform.api.PluginDatabaseManager
import com.panomc.platform.api.config.PluginConfigManager
import com.panomc.platform.setup.SetupManager
import com.panomc.plugins.faq.config.FAQConfig

class FAQPlugin : PanoPlugin() {
    private val pluginDatabaseManager by lazy {
        applicationContext.getBean(PluginDatabaseManager::class.java)
    }

    private val setupManager by lazy {
        applicationContext.getBean(SetupManager::class.java)
    }

    private var isInitialized = false

    override suspend fun onStart() {
        logger.info("Starting...")

        startPlugin()
    }

    internal suspend fun startPlugin() {
        if (isInitialized) return

        // The config file does not need the database: it is there before setup is done, so the config endpoints
        // work from the start (they failed with a missing bean until setup was finished).
        if (!pluginBeanContext.beanFactory.containsSingleton(PluginConfigManager::class.java.name)) {
            val configManager = PluginConfigManager(this, FAQConfig::class.java)
            pluginBeanContext.beanFactory.registerSingleton(PluginConfigManager::class.java.name, configManager)
        }

        if (!setupManager.isSetupDone()) {
            logger.info("Setup is not finished, waiting for setup completion...")
            return
        }

        isInitialized = true

        pluginDatabaseManager.initialize(this)
        
        logger.info("Started!")
    }

    override suspend fun onEnable() {
        logger.info("Enabled!")
    }

    override suspend fun onDisable() {
        isInitialized = false
    }

    override suspend fun onUninstall() {
        pluginDatabaseManager.uninstall(this)
    }
}
