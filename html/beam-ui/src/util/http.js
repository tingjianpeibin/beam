import axios from 'axios'
import {commonParams} from './config'
import {Message} from 'element-ui'

const Http = {
    //导出
    export(url, params, fileName) {
        url = "/beam_ht" + url;
        return axios({
            method: "get",
            url: url,
            params: params,
            responseType: "blob"
        }).then((res) => {
            let blob = new Blob([res.data], {type: "application/vnd.ms-excel;charset=utf-8"});
            if (window.navigator.msSaveOrOpenBlob) {
                navigator.msSaveBlob(blob, fileName);
            } else {
                let link = document.createElement("a");
                link.href = window.URL.createObjectURL(blob);
                link.download = fileName;
                link.click();
                window.URL.revokeObjectURL(link.href);
            }
        })
    },

    //get请求
    get(url, params) {
        const data = Object.assign({}, commonParams, params);
        url = "/beam_ht" + url;
        return axios.get(url, {
            params: data,
        }).then((res) => {
            if (res.data.error === false) {
                return Promise.resolve(res.data); //成功
            } else {
                if (res.data.code === -1) {
                    Message.error(res.data.msg);
                    window.location = "/#/login";
                    return Promise.resolve(res) //拒绝
                } else if (res.data.code === 403) {
                    Message.error(res.data.msg);
                    window.location = "/#/403";
                    return Promise.resolve(res) //拒绝
                } else {
                    Message.error(res.data.msg);
                    return Promise.resolve(res) //拒绝
                }
            }
        }).catch((err) => {
            //超时之后在这里捕抓错误信息.
            if (err.response) {
                var res = {
                    code: err.code,
                    msg: err.message,
                };
                return Promise.reject(res)
            } else if (err.request) {
                if (err.request.readyState === 4 && err.request.status === 0) {
                    //我在这里重新请求
                    var res = {
                        code: 403,
                        msg: "网络链接错误，请刷新重试！",
                    };
                    this.$message.error("网络链接错误，请刷新重试！");
                    return Promise.reject(res)
                }
            } else {
                Message.error(err.message);
                return Promise.reject(res)
            }
            return Promise.reject(err)
        })
    },

    //post请求
    post(url, data) {
        axios.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
        url = "/beam_ht" + url;
        return axios.post(url, data).then((res) => {
            if (res.data.error === false) {
                return Promise.resolve(res.data); //成功
            } else {
                if (res.data.code === -1) {
                    Message.error(res.data.msg);
                    window.location = "/#/login";
                    return Promise.resolve(res) //拒绝
                } else if (res.data.code === 403) {
                    Message.error(res.data.msg);
                    window.location = "/#/403";
                    return Promise.resolve(res) //拒绝
                } else {
                    Message.error(res.data.msg);
                    return Promise.resolve(res) //拒绝
                }
            }
        }).catch((err) => {
            //超时之后在这里捕抓错误信息.
            if (err.response) {
                var res = {
                    code: err.code,
                    msg: err.message,
                };
                return Promise.reject(res)
            } else if (err.request) {
                if (err.request.readyState === 4 && err.request.status === 0) {
                    //我在这里重新请求
                    var res = {
                        code: 403,
                        msg: "网络链接错误，请刷新重试！",
                    };
                    this.$message.error("网络链接错误，请刷新重试！");
                    return Promise.reject(res)
                }
            } else {
                Message.error(err.message);
                return Promise.reject(res)
            }
            return Promise.reject(err)
        })
    },
};

export default Http;
