import React, { useEffect, useState } from 'react';
import './Dashboard.css';
import Navbar from '../LandingPage/Navbar';
import { AiOutlineFire } from 'react-icons/ai';
import { IoMdTrendingUp, IoMdTrendingDown } from 'react-icons/io';
import { BiNews } from 'react-icons/bi';


const Dashboard = () => {
  const [user, setUser] = useState({ fullName: '' });

  useEffect(() => {
    // Retrieve the entire user object from localStorage
    const storedUser = JSON.parse(localStorage.getItem('user'));
    if (storedUser) {
      setUser(storedUser); // Set the user state with the retrieved user object
    }
  }, []);
  const [trendingCoins, setTrendingCoins] = useState([]);
  const [topCoins, setTopCoins] = useState([]);

  useEffect(() => {
    fetchTrendingCoins();
    fetchTop50Coins();
  }, []);

  const fetchTrendingCoins = async () => {
    try {
      const response = await fetch('http://localhost:8005/auth/coins/trending', {
        method: 'GET',
        credentials: 'same-origin', // This ensures cookies are sent with the request
      });
      const data = await response.json();
      setTrendingCoins(data.coins); // Adjust key if API returns differently
    } catch (error) {
      console.error('Error fetching trending coins:', error);
    }
  };

  const fetchTop50Coins = async () => {
    try {
      const response = await fetch('http://localhost:8005/auth/coins/top10', {
        method: 'GET',
        credentials: 'same-origin', // This ensures cookies are sent with the request
      });
      const data = await response.json();
      setTopCoins(data); // Adjust key if API returns differently
    } catch (error) {
      console.error('Error fetching top 50 coins:', error);
    }
  };

  return (
    <div className="dashboard">
      <Navbar />
      <header className="dashboard-header">
        <div className="user-info">
          <h2>Hi, {user.fullname} 👋</h2>
          <p>Let's grow your stocks with SafeCryptoStocks</p>
        </div>
      </header>
      <main className="dashboard-content">
        {/* Top Trending Coins Section */}
        <section className="trending-coins">
          <h3>
            <AiOutlineFire style={{ color: '#f39c12', fontSize: '1.5rem' }} /> Top Trending Coins
          </h3>
          <ul>
            {trendingCoins.map((coin, index) => (
              <li key={index}>
                <img src={coin.item.small} alt={coin.item.name} style={{ width: '20px', marginRight: '10px' }} />
                {coin.item.name} <span>Market Rank {coin.item.market_cap_rank}</span>
              </li>
            ))}
          </ul>
        </section>

        {/* Top Cryptocurrencies by Market Cap */}
        <section className="top-cryptocurrencies">
          <h3>Top Cryptocurrencies by Market Cap</h3>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Name</th>
                <th>Price</th>
                <th>24hrs</th>
                <th>7 Days</th>
              </tr>
            </thead>
            <tbody>
              {topCoins.map((coin, index) => (
                <tr key={index}>
                  <td>{index + 1}</td>
                  <td>
                    <img src={coin.image} alt={coin.name} style={{ width: '20px', marginRight: '10px' }} />
                    {coin.name}
                  </td>
                  <td>${coin.current_price.toLocaleString()}</td>
                  <td className={coin.price_change_percentage_24h > 0 ? 'value-positive' : 'value-negative'}>
                    {coin.price_change_percentage_24h > 0 ? (
                      <IoMdTrendingUp style={{ color: '#28a745' }} />
                    ) : (
                      <IoMdTrendingDown style={{ color: '#dc3545' }} />
                    )}
                    {coin.price_change_percentage_24h.toFixed(2)}%
                  </td>
                  <td className={coin.ath_change_percentage > 0 ? 'value-positive' : 'value-negative'}>
                    {coin.ath_change_percentage > 0 ? (
                      <IoMdTrendingUp style={{ color: '#28a745' }} />
                    ) : (
                      <IoMdTrendingDown style={{ color: '#dc3545' }} />
                    )}
                    {coin.ath_change_percentage.toFixed(2)}%
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

        </section>
        {/* Latest News Section */}
        <section className="latest-news">
          <h3>
            <BiNews style={{ color: '#007bff' }} /> Latest News
          </h3>
          <div>
            <p>
              Trump is launching a cryptocurrency platform, and we have no idea what it does. <a href="/news">Read More</a>
            </p>
            <p>
              The number of bitcoin millionaires has increased by 11% in the last year as the cryptocurrency rallies.
            </p>
          </div>
        </section>

      </main>
    </div>
  );
};

export default Dashboard;

