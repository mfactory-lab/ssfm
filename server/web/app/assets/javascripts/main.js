/**
 * Copyright 2016, Alexander Ray (dev@alexray.me)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 **/

requirejs.config({
    map: {
        '*': {
            'css': '/webjars/require-css/0.1.8/css.js' // or whatever the path to require-css is

        }
    },

    shim: {
        'jsRoutes': {
            deps: [],
            exports: 'jsRoutes'
        },
        
        'angular': {
            exports: 'angular'
        },

        'jquery': {
            exports: 'jquery'
        },

        'bootstrap': {
            deps: ['jquery']
        },

        'adminLte': {
            deps: ['jquery', 'bootstrap']
        }
    },

    paths: {
        'jsRoutes': ['/jsroutes'],
        'scalaJsApp': [
            function() {
                if (window.isProduction) {
                    return '/assets/scalajs-opt'
                } else {
                    return '/assets/scalajs-fastopt'
                }
            }()

        ],
        'bootstrap': ['/assets/lib/angular-ui-bootstrap/dist/ui-bootstrap'],
        'jquery': ['/assets/lib/jquery/jquery.min'],
        'adminLte': ['/assets/lib/adminlte/dist/js/app.min']
    }

});

requirejs([
    'angular',
    'scalaJsApp',
    './common/services/playRoutesService',
    'bootstrap',
    'jquery',
    'adminLte'
], function (angular)
{
    ((typeof global === "object" && global && global["Object"] === Object) ? global : this)["App"]().main();

    angular.bootstrap(document, ["ssfm-module.scalajs-app"]);

});