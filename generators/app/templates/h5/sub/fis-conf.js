/**
 * Created by hale_v on 16/4/5.
 */

// 启用 fis-spriter-csssprites 插件
fis.match('::package', {
  postpackager: fis.plugin('loader'),
  spriter: fis.plugin('csssprites')
});

fis.match('*.less', {
  parser: fis.plugin('less'),
  rExt: '.css'
});

// 对 CSS 进行图片合并
/*fis.match('{*.less,*.css}', {
 useSprite: true
 });*/

fis.match('*.es6', {
  // fis3-parser-babel 插件进行解析
  parser: fis.plugin('babel'),
  // .es6 文件后缀构建后被改成 .js 文件
  rExt: '.js'
});

fis.match(/^\/src\/(.*)/i,{
  release:'./$1'
});

fis.match('!*.html', {
  useHash: true,
  domain: 'http://s.flyfinger.com/<%= props.name %>/<%= props.subName %>'
});

fis.match('{*.js,*.es6}', {
  optimizer: fis.plugin('uglify-js')
});

fis.match('{*.css,*.less}', {
  optimizer: fis.plugin('clean-css')
});

/*fis.match('*.png', {
 optimizer: fis.plugin('png-compressor')
 });*/

fis.match('css/(**.png)', {
  release:'images/$1'
});

/*fis.match('js/common/!*.js',{
 packTo:'js/common/aio.js'
 });*/

fis.match('{gulpfile.js,package.json,abc.json,**/node_modules/**,deploy.bat,Gruntfile.js,README.md,.gitignore,gitignore}',{
  release:false
});


fis.media('demo').match('*',{
  useHash:false,
  optimizer:null,
  useSprite:false,
  packTo:false,
  domain:false
});
fis.media('debug').match('*',{
  domain:false
});
