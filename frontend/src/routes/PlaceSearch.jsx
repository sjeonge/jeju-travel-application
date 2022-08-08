import Header from "components/Header/Header"
import { useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import axios from "axios"
import SearchBody from "components/PlaceSearch/SearchBody"
import SelectedSpots from "components/PlaceSearch/SelectedSpots"
import RecommendList from "components/PlaceSearch/RecommendList"
import "./placesearch.css"

function PlaceSearch() {
  const navigate = useNavigate()
  const [spotSearch, setSpotSearch] = useState("")
  const [resultLst, setresultLst] = useState([])
  const selectedSpots = useSelector(state => state.selectedSpots)
  
  const inputSearch = async(query) => {
    const response = await axios.get(`http://i7a609.p.ssafy.io:8081/api/v1/place/find?q=${query}`)
    setresultLst(response.data.findPlaces)
  }

  const handleSpotChange = (e) => {
    setSpotSearch(e.target.value)
    if (e.target.value) {
      inputSearch(e.target.value)
    }
  }

  const [ containerStyle, setContainerStyle ] = useState({
    borderBottom: "2px solid black"
  })

  const handleFocus = () => {
    setContainerStyle({
      ...containerStyle,
      borderBottom: "2px solid #1E88E5"
    })
  }

  const handleFocusOut = () => {
    setContainerStyle({
      ...containerStyle,
      borderBottom: "2px solid black"
    })
  }

  const handleInputBtn = () => {
    navigate("/address")
  }

  const handleSubmit = () => {
    navigate("/travel")
    // 장소 추가하는 action
  }

  return (
    <>
      <Header style={{ margin: "3vh 4vw"}} />
      <div className="text-center">
        <img className="search-icon" alt="searchIcon" src="icons/searchIcon.png" />
        <div style={containerStyle} className="search-input-container">
          <input autoFocus className="search-input"
            value={spotSearch}
            onChange={handleSpotChange} 
            onFocus={handleFocus} 
            onBlur={handleFocusOut}
          />
        </div>
      </div>
      { selectedSpots.length === 0 ? null : <SelectedSpots /> }
      <SearchBody spotSearch={spotSearch} resultLst={resultLst} />
      { selectedSpots.length !== 0 && resultLst.length === 0 && spotSearch &&
        <div className="text-center">
        <p onClick={handleInputBtn} className="content-size" style={{ marginTop: "5vh", cursor: "pointer"}}>장소 직접 추가하러 가기!</p>
        <p className="content-size gray">지금까지 선택된 장소는 자동으로 일정에 추가돼요.</p>
        </div>
      }
      { selectedSpots.length === 0 && resultLst.length === 0 && spotSearch &&
        <p className="content-size gray text-center">찾는 장소가 없다면 직접 입력해보세요!</p>
      }
      { selectedSpots.length === 0 
       ? <button onClick={handleInputBtn} className="place-btn block" style={{margin: "5vh auto"}}>직접입력하기</button>
       : <button onClick={handleSubmit} className="place-btn block" style={{margin: "5vh auto"}}>선택완료</button> 
      }
      { selectedSpots.length === 0 && resultLst.length === 0 && spotSearch &&
        <div>
          <hr />
          <RecommendList />
        </div>
      }
    </>
  )
}

export default PlaceSearch