// 加载图像 页面初始加载进度
var ImageLoad = function(){
  this.images = []; // 保存加载成功的图片信息
  this.imageUrls = []; // 图片地址
  this.imagesLoaded = 0; //
  this.imagesFailedToLoad = 0;
  this.imageLoadingProgress = 0;
  this.imageLen = 0;
};

ImageLoad.prototype = {
  queueImage: function(imageUrls){
    var _this = this;

    if(Object.prototype.toString.call(imageUrls) === '[object Array]'){
      imageUrls.forEach(function(value, index, array){
        _this.imageUrls.push(value);
      });
    }else{
      this.imageUrls.push(imageUrls);
    }

    this.imageLen = this.imageUrls.length;

    return this;
  },
  preLoad: function(progressCallback, loadAfterCallback){
    this.loadImages();
    this.imageLoadingProgressCallback(progressCallback, loadAfterCallback);
  },
  loadImages: function(){
    var _this = this;

    this.imageUrls.forEach(function(value, index, array) {
      var img = new Image();

      img.src = value;

      if(img.complete) {
        _this.images[index] = img;
        _this.imagesLoaded++;
      }
      else {
        img.onload = (function(JIndex) {
          return function() {
            _this.images[JIndex] = img;
            _this.imagesLoaded++;
          };
        })(index);

        img.onerror = function() {
          _this.imagesFailedToLoad++;
        };
      }
    });
    return this;
  },
  imageLoadingProgressCallback: function(progressCallback, loadAfterCallback){
    var _this = this,
      interval = setInterval(function(){
        var progress = Math.floor((_this.imagesLoaded + _this.imagesFailedToLoad) / _this.imageLen * 100);

        _this.imageLoadingProgress = isNaN(progress) ? 100 : progress;

        if(_this.imageLoadingProgress === 100){
          clearInterval(interval);
          setTimeout(function(){
            loadAfterCallback.call(_this);
            _this.imagesLoaded = 0;
            _this.imagesFailedToLoad = 0;
          }, 300);
        }
        progressCallback.call(_this, _this.imageLoadingProgress);
      }, 10);
  },
  getLoadAfterImages: function(){
    return this.images;
  }
};

window.ImageLoad = ImageLoad;
