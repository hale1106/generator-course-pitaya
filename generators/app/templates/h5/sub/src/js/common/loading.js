/**
 * Created by hale on 15/5/17.
 * Modify by yuqiang on 2015/11/16
 */
// 加载图片
(function(){
  var imageLoad = new ImageLoad(),
    imageUrls = [],
    $loading = $('#loading'),
    _main = null;

  $(document.body).find('img').each(function(){
    imageUrls.push($(this).attr('src'));
  });

  imageLoad.queueImage(imageUrls).queueImage(loadImgArr).preLoad(function(progress){
    $loading.find('.progress span').html(progress + '%');
    $loading.find('.progress_bar span').css({
      width: progress + '%'
    });
  }, function(){
    $loading[0] && $loading.remove();

    _main = new Main().init();

    // 二次检测图片有没有加载，没加载的加载图片
    this.loadImages();
  });
})();

