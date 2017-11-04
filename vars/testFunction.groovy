def call(string testoutput) {

    stage name: 'Stage', concurrency: 1 {
        print "yo"
        print "Output: ${config.testoutput}"
    }
}