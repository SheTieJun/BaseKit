package me.shetj.base.base

/**
 * Base Controller Functions used in activities, fragments, dialogs, bottomsheet
 */
interface BaseControllerFunctionsImpl {


    /**
     * Init base view
     * i.e. setTitle, setToolbar, setBackButton, etc.
     */
    fun initBaseView(){}

    /**
     * All initialization related work will be done in this method.
     * i.e. Handling lifecycle methods.
     */
    fun onInitialized() {}

    /**
     * All observer listener code will be handled in this method inside controllers.
     */
    fun addObservers() {}

    /**
     * All click action code will be handled in this method inside controllers.
     */
    fun setUpClicks() {}

}