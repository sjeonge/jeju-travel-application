import axios from "axios"
import { addSchedule, addTravelInfo, initAuthorities, initSchedules, release, terminate } from "../stateManager.js"
import { APIS } from "../apiHandler.js"

// ===== Fetch API requests start =====
const fetchTravelInfo = async (travelId, token) => {
  const response = await axios({
    method: "get",
    url: `${APIS.HOST_SERVER}/trip/showTripInfo/${travelId}`,
    validateStatus: status => status === 200,
    headers: {
      Authorization: token
    }
  })

  return response.data.tripInfo
}

const fetchSchedule = async (day, travelId, token) => {
  const response = await axios({
    method: "get",
    url: `${APIS.HOST_SERVER}/schedule?day=${day}&tripId=${travelId}`,
    validateStatus: status => status === 200,
    headers: {
      Authorization: token
    }
  })

  return response.data["일자별 Schedule List"]
}

const fetchAllSchedule = (travelId, roomTable, token) => {
  const { periodInDays } = roomTable[travelId].travelInfo

  const promises = []

  for (let day = 0; day < periodInDays; day++) {
    promises.push(fetchSchedule(day, travelId, token))
  }

  return Promise.all(promises)
}

const fetchTravel = async (travelId, roomTable, token) => {
  const travelInfo = await fetchTravelInfo(travelId, token)
  
  addTravelInfo(travelId, roomTable, travelInfo)
  initSchedules(travelId, roomTable, travelInfo)
  initAuthorities(travelId, roomTable, travelInfo)

  const schedules = await fetchAllSchedule(travelId, roomTable, token)
  schedules.forEach((scheduleList, day) => {
    addSchedule(travelId, roomTable, scheduleList, day)
  })

  roomTable[travelId].fetched = true
  release(travelId, roomTable)
  return
}
// ===== Fetch API requests end =====

export { fetchTravel, fetchTravelInfo }