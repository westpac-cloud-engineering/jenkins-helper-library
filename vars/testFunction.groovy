def call(String testoutput) {

    stage name: 'Stage', concurrency: 1 {
        print "yo"
        print "Output: ${testoutput}"
    }
}