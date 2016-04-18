/**
 * Created by hale_v on 16/4/5.
 */
fis.require('jello')(fis);

// 启用 fis-spriter-csssprites 插件
fis.match('::package', {
 spriter: fis.plugin('csssprites')
 });

fis.match('*.less', {
  // fis-parser-less 插件进行解析
  parser: fis.plugin('less'),
  // .less 文件后缀构建后被改成 .css 文件
  rExt: '.css'
});

// 对 CSS 进行图片合并
fis.match('{*.less,*.css}', {
  useSprite: true
});
fis.config.set('settings.spriter.csssprites', {
  //图之间的边距
  margin: 10
});

fis.match('static/**', {
  useHash: true,
  domain:'http://h5.flyfinger.com/<%= props.name %>'
});

fis.match('*.js', {
  optimizer: fis.plugin('uglify-js')
});

fis.match('{*.css,*.less}', {
  optimizer: fis.plugin('clean-css')
});

/*fis.match('*.png', {
 optimizer: fis.plugin('png-compressor')
 });*/

fis.match('static/css/(**.png)', {
  release:'static/images/$1'
});

fis.media('debug').match('::packager', {
  packager: fis.plugin('deps-pack', {

    'static/js/pkg/global.js': [
      'static/js/common/zepto.js',
      'static/js/common/dialog.js'
    ]
  })
});

fis.media('demo').match('*',{
  useHash:false,
  optimizer:null,
  useSprite:false,
  packTo:false,
  domain:false
});

fis.media('debug').match('static/**',{
  domain:false
});