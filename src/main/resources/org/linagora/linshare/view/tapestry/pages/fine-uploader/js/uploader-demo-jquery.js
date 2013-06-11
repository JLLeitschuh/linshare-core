jQuery(document).ready(function() {
    var errorHandler = function(event, id, fileName, reason, xhr) {
        qq.log("id: " + id + ", fileName: " + fileName + ", reason: " + reason);
    };

    var fileNum = 0;

    jQuery('#basicUploadSuccessExample').fineUploader({
        debug: true,
//        cors: {
//            expected: true
//        },
        request: {
            endpoint: "/upload/receiver",
            paramsInBody: true
//            params: {
//                test: 'one',
//                blah: 'foo',
//                bar: {
//                    one: '1',
//                    two: '2',
//                    three: {
//                        foo: 'bar'
//                    }
//                },
//                fileNum: function() {
//                    fileNum+=1;
//                    return fileNum;
//                }
//            }
        },
        chunking: {
            enabled: true
        },
        resume: {
            enabled: true
        },
        retry: {
            enableAuto: true,
            showButton: true
        },
        deleteFile: {
            enabled: true,
            endpoint: '/upload/receiver',
            forceConfirm: true,
            params: {foo: "bar"}
        },
        display: {
            fileSizeOnSubmit: true
        },
        paste: {
            targetElement: jQuery(document)
        }
    })
        .on('error', errorHandler)
        .on('uploadChunk resume', function(event, id, fileName, chunkData) {
            qq.log('on' + event.type + ' -  ID: ' + id + ", FILENAME: " + fileName + ", PARTINDEX: " + chunkData.partIndex + ", STARTBYTE: " + chunkData.startByte + ", ENDBYTE: " + chunkData.endByte + ", PARTCOUNT: " + chunkData.totalParts);
        })
        .on("upload", function(event, id, filename) {
            jQuery(this).fineUploader('setParams', {"hey": "ho"}, id);
        });
//        on("pasteReceived", function(event, blob) {
//            qq.log(blob);
//            return new qq.Promise().success();
//        });

    jQuery('#manualUploadModeExample').fineUploader({
        autoUpload: false,
        debug: true,
        uploadButtonText: "Select Files",
        request: {
            endpoint: "/upload/receiver"
        },
        display: {
            fileSizeOnSubmit: true
        }
    }).on('error', errorHandler);

    jQuery('#triggerUpload').click(function() {
        jQuery('#manualUploadModeExample').fineUploader("uploadStoredFiles");
    });


    jQuery('#basicUploadFailureExample').fineUploader({
        request: {
            endpoint: "/upload/receiver",
            params: {"generateError": "true"}
        },
        debug: true,
        failedUploadTextDisplay: {
            mode: 'custom',
            maxChars: 5
        },
        retry: {
            enableAuto: true,
            showButton: true
        }
    }).on('error', errorHandler);


    jQuery('#uploadWithVariousOptionsExample').fineUploader({
        multiple: false,
        request: {
            endpoint: "/upload/receiver"
        },
        debug: true,
        validation: {
            allowedExtensions: ['jpeg', 'jpg', 'txt'],
            sizeLimit: 50000,
            minSizeLimit: 2000
        },
        text: {
            uploadButton: "Click Or Drop"
        },
        display: {
            fileSizeOnSubmit: true
        }
    }).on('error', errorHandler);


    jQuery('#fubExample').fineUploader({
        uploaderType: 'basic',
        multiple: false,
        debug: true,
        autoUpload: false,
        button: jQuery("#fubUploadButton"),
        request: {
            endpoint: "/upload/receiver"
        }
    }).on('error', errorHandler);
});
