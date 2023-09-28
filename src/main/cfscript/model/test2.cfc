component {

    /**
     * This is a test cffile to test the watcher.
     */
    public any function change() {
        //this is a change
        throw("Sub changed"); // here

        return 1;
    }
}