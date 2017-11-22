var gulp = require('gulp');

const PROD_DEST = './dist';

gulp.task('default', function () {
    return gulp.src(['./node_modules/**/*'])
        .pipe(gulp.dest(PROD_DEST + "/node_modules"));
});