import request from '@/utils/request'

export function getpageList(data) {
  return request({
    url: '/pay/order/collectlist',
    method: 'POST',
    data
  })
}

export function getTimes(data) {
  return request({
    url: '/pay/order/queryTime',
    method: 'POST', 
    data
  })
}

 