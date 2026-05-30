package de.snenjih.velocloud.agent.detector

interface Detector {

    fun tick()

    fun cycleLife() : Long

}