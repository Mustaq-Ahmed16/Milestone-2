import React from "react";
import Navbar from "../LandingPage/Navbar.jsx";
import MainSection from "../LandingPage/MainSection.jsx";
// import DomainSearch from "../LandingPage/DomainSearch.jsx";

function LandingPage() {
  return (
    <div className="App">
      <Navbar />
      <MainSection />
      {/* <DomainSearch /> */}
    </div>
  );
}

export default LandingPage;
