var gulp = require('gulp');
var imagemin = require('gulp-imagemin');
var pngquant = require('imagemin-pngquant');
var exec = require('gulp-exec');
var cache = require('gulp-cache');
var process =require('child_process');
var clean = require('gulp-clean');
var abcpkg = require('./abc.json');
var _process = require('process');
var os = require('os');

var execOption = {
  options : {
    continueOnError: true, // default = false, true means don't emit error event
    pipeStdout: true // default = false, true means stdout is written to file.contents,
  },
  reportOptions : {
    err: true, // default = true, false means don't write err
    stderr: true, // default = true, false means don't write stderr
    stdout: true // default = true, false means don't write stdout
  }
};

var platform = os.type();

//exec命令
var demoExec = 'fis3 server clean;fis3 release demo;fis3 server start -p '+abcpkg.port+' --type jello;fis3 release demo -w';
var demoExecWin = 'fis3 server clean & fis3 release demo & fis3 server start -p '+abcpkg.port+' --type jello & fis3 release demo -w';
var debugExec = 'fis3 server clean; fis3 release debug;fis3 server start -p '+abcpkg.port+' --type jello;fis3 release debug -w';
var debugExecWin = 'fis3 server clean & fis3 release debug & fis3 server start -p '+abcpkg.port+' --type jello & fis3 release debug -w';
var destExec = 'fis3 release -d ./build';


//压缩图片任务
gulp.task('imagemin', function(cb){
  gulp.src('build/images/**')
    .pipe(cache(imagemin({
      progressive: true,
      svgoPlugins: [{removeViewBox: false}],
      use: [pngquant()]
    })))
    .pipe(gulp.dest('build/images'))
    .on('finish',cb);
});

//启动demo环境
gulp.task('demo',function(cb){
  //是否是windows
  var _exec = platform == 'Windows_NT' ? demoExecWin : demoExec;
  var ls = process.exec(_exec,function(err,s,t){
    cb();
  });
  ls.stdout.on('data', function (data) {
    _process.stdout.write(data);
  });
  ls.stderr.on('data', function (data) {
    _process.stdout.write('stderr: ' + data);
  });
});


//启动debug环境
gulp.task('debug',function(cb){
  //是否是windows
  var _exec = platform == 'Windows_NT' ? debugExecWin : debugExec;
  var ls = process.exec(_exec,function(err,s,t){
    cb(err);
  });
  ls.stdout.on('data', function (data) {
    _process.stdout.write(data);
  });
  ls.stderr.on('data', function (data) {
    _process.stdout.write('stderr: ' + data);
  });
});


//清除build任务
gulp.task('clean',function(cb){
  gulp.src('./build',{read:false})
    .pipe(clean())
    .on('finish',cb);
});

//输出build任务
gulp.task('output',function(cb){
  gulp.src('./abc.json')
    .pipe(exec(destExec,execOption.options))
    .pipe(exec.reporter(execOption.reportOptions))
    .on('finish',cb);
});

//综合编译任务
gulp.task('build',gulp.series('output','clean','output','imagemin'));

