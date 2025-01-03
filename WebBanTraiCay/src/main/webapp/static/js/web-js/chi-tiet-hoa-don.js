'use strict'

var cloudName = 'dter3mlpl';
var apiKey = '899244476586798';
var cl = cloudinary.Cloudinary.new({cloud_name: cloudName});

$('.table-ds-hoa-don .img-default').each(
    (_, elements) => {
      const publicId = $(elements).data('assets');
      const imageUrl = publicId ? cl.url(publicId) : null;
      const imgDefault = `${window.context}/static/images/default-fruit.jpg`;
      if (imageUrl !== null) {
        $(elements).find('img').prop('src', imageUrl);
      } else {
        $(elements).find('img').prop('src', imgDefault);
      }
    });
