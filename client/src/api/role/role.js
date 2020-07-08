import request from '@/utils/request'

export function getpageList(data) {
  return request({
    url: '/pay/role/list',
    method: 'POST',
    data
  })
}

export function edit(data) {
  return request({
    url: '/pay/role/edit',
    method: 'POST',
    data
  })
}

export function del(data) {
  return request({
    url: '/pay/role/delete',
    method: 'POST',
    data
  })
}

export function info(data) {
  return request({
    url: '/pay/role/info',
    method: 'POST',
    data
  })
}

export function setTree(data) {
  return request({
    url: '/pay/role/setTree',
    method: 'POST',
    data
  })
}

export function getTree(data) {
  return request({
    url: '/pay/role/getTree',
    method: 'POST',
    data
  })
}


