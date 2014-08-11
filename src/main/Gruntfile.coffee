module.exports = (grunt) ->
    mapName = (fileName) ->
        console.log fileName
        fileName + '.map'

    grunt.initConfig
        pkg: grunt.file.readJSON 'package.json'
        watch:
            files: ['public/notifications/imbox/coffee/*.coffee']
            tasks: ['coffee', 'uglify']
        coffee:
            compile:
                files: [
                    expand: true
                    cwd: 'public/notifications/imbox/coffee/'
                    src: ['*.coffee']
                    dest: 'public/notifications/imbox/js/'
                    ext: '.js'
                ]
                options:
                    sourceMap: true
        uglify:
            compress_target:
                files: [
                    expand: true,
                    cwd: 'public/notifications/imbox/js'
                    src: ['*.js', '!*.min.js']
                    dest: 'public/notifications/imbox/js'
                    ext: '.min.js'
                ]
                options:
                    sourceMapIncludeSources: true
                    sourceMapIn: mapName
                    sourceMap: true
        docco:
            debug:
                src: ['public/notifications/imbox/coffee/*.coffee']
                options:
                    output: '../../docs'

    grunt.loadNpmTasks 'grunt-contrib-coffee'
    grunt.loadNpmTasks 'grunt-contrib-watch'
    grunt.loadNpmTasks 'grunt-contrib-uglify'
    grunt.loadNpmTasks 'grunt-docco'
    grunt.registerTask 'default', ['watch']
    grunt.registerTask 'compile', ['coffee', 'uglify', 'docco']
    return

