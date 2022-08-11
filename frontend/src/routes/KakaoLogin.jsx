import "./Login.css"
import "globalStyle.css"


const URL = "https://i7a609.p.ssafy.io/oauth2/authorization/kakao"

function KakaoLogin() {

  return (
    <div className="text-center">
      <p className="login-title-font block">놀멍쉬멍</p>
      <p className="login-subtitle-font">JEJU TRAVEL APP</p>
      <img className="hareu-size" alt="하르방" src="icons/hareubang.png" />
      <a href={URL}>
        <img className="kakao-box" alt="kakaoLoginBtn" src="icons/kakaoLogo.png"></img>
      </a>
    </div>
  )
}

export default KakaoLogin