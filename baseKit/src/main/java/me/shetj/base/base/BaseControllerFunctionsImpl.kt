package me.shetj.base.base

/**
 * Base Controller Functions used in activities, fragments, dialogs, bottomsheet
 */
interface BaseControllerFunctionsImpl {


    /**
     * step 1
     * Init base view
     * i.e. setTitle, setToolbar, setBackButton, etc.
     */
    fun initBaseView(){}

    /**
     * step 2
     * All initialization related work will be done in this method.
     * i.e. Handling lifecycle methods.
     */
    fun onInitialized() {}

    /**
     * step 3
     * All observer listener code will be handled in this method inside controllers.
     */
    fun addObservers() {}

    /**
     * step 4
     * All click action code will be handled in this method inside controllers.
     */
    fun setUpClicks() {}

}