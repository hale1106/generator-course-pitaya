/**
 * FileUpload javascript Library v0.0.1
 *
 * date: 2016-1-7
 */

(function(){
  'use strict';

  /**
   * @class FileUpload
   * @param config
   * @constructor
   */
  var FileUpload = function(config){
    if(!(this instanceof FileUpload)){
      return new FileUpload(config);
    }

    this.fileFilter = []; // 过滤后的文件数组
    this.fileLen = 0; // 文件上传个数
    this.init(config);
  };

  FileUpload.prototype = {
    constructor: 'FileUpload',
    init: function(config){
      var _this = this;

      _this.config = $.extend({
        fileElem: $('input[type="file"]'),
        FORMAT: 'jpg,jpeg,png,gif', // 限制格式
        MAX_IMAGE_SIZE: 2 * 1024 * 1024, // 上传图片大小上限 2MB
        url: '',
        onReader: function(e, file){},
        onProgress: function(file, progress){},
        onSuccess: function(file, data){},
        onFailure: function(file, data){},
        onComplete: function(){}
      }, config || {});

      this._$file = _this.config.fileElem;

      this._$file.parent().on('change', this._$file, function(e){
        e.stopPropagation();
        _this.funGetFile(e, $(this).find('input'));
      });
    },
    updateInput: function(fileElem){
      fileElem[0].outerHTML = fileElem[0].outerHTML.replace(/(value=\").+\"/i, "$1\"");
    },
    /* 获取文件 */
    funGetFile: function(e, fileElem){
      var _this = this,
        files = e.target.files,
        index = 0;

      this.fileFilter = this.fileFilter.concat(this.filter(files, fileElem));
      this.fileLen = this.fileFilter.length;

      function funAppendImage(){
        var file = _this.fileFilter[index];

        if(file){
          var reader = new FileReader();

          reader.onload = function(e){
            file.timeStamp = e.timeStamp;
            file.result = e.target.result;

            _this.config.onReader.call(_this, e, file);
            _this.funUploadFile(file, fileElem);

            index++;
            funAppendImage();
          };

          reader.readAsDataURL(file);
        }
      }

      funAppendImage();
    },
    /* 上传文件 */
    funUploadFile: function(file, fileElem){
      var _this = this,
        xhr = new XMLHttpRequest(),
        formData = new FormData();

      if(xhr.upload){
        xhr.upload.addEventListener('progress', function(e){
          var progress = (e.loaded / e.total * 100);

          _this.config.onProgress.call(_this, file, progress);
        }, false);

        xhr.onreadystatechange = function(e) {
          if (xhr.readyState == 4) {
            if (xhr.status == 200) {
              _this.config.onSuccess.call(_this, file, $.parseJSON(xhr.responseText));
              _this.fileLen--;

              if (_this.fileLen <= 0) {
                _this.config.onComplete.call(_this);
                _this.fileFilter = [];
                _this.updateInput(fileElem);
              }
            } else {
              _this.config.onFailure.call(_this, file, $.parseJSON(xhr.responseText));
              _this.fileFilter = [];
              _this.updateInput(fileElem);
            }
          }
        };

        formData.append("file", file);
        xhr.open("POST", _this.config.url, true);
        xhr.send(formData);
      }
    },
    /*
     * 过滤函数
     * @param {Object} [files] 文件
     */
    filter: function(files, fileElem){
      var arrFiles = [];

      for(var i = 0, file; file = files[i]; i++){
        if(this.formatRegExp(file.name)){
          if(file.size > this.config.MAX_IMAGE_SIZE){
            alert('亲，您上传的文件太大了！！！');
            this.updateInput(fileElem);
            return [];
          }else{
            arrFiles.push(file);
          }
        }else{
          alert('亲，您上传文件的格式不符合规则哦！！！');
          this.updateInput(fileElem);
          return [];
        }
      }
      return arrFiles;
    },
    /*
     * 限制格式检测
     * @param {String} [name] 上传文件name
     */
    formatRegExp: function(name){
      var format = this.config.FORMAT.split(',').join('|');
      return new RegExp('\.('+ format +')$', 'i').test(name);
    }
  };

  window.FileUpload = FileUpload;
})();



// 上传图片
/*
 * 示例：
 var _fileUpload = FileUpload({
 fileElem: $('.image_upload_file'),
 url: '',
 onReader: function(e, file){
 // 上传前读取上传文件属性
 },
 onProgress: function(file, progress){
 // 上传进度条
 },
 onSuccess: function(file, data){
 // 上传成功
 },
 onFailure: function(file, data){}
 });
 * */

