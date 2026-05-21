import { useEffect, useState } from "react";
import axios from "axios";

import { supabase } from "./supabase";
import Login from "./components/Login";

import hotel1 from "./assets/hotel1.jpg";
import hotel2 from "./assets/hotel2.jpg";
import hotel3 from "./assets/hotel3.jpg";
import hotel4 from "./assets/hotel4.jpg";
import hotel5 from "./assets/hotel5.jpg";
import hotel6 from "./assets/hotel6.jpg";

import "./App.css";

function App() {
  const [session, setSession] = useState(null);

  const userRole = session?.user?.user_metadata?.role;
  const isAdmin = userRole === "ADMIN";

  const [hotels, setHotels] = useState([]);
  const [city, setCity] = useState("");
  const [startDate, setStartDate] = useState("2026-06-02");
  const [endDate, setEndDate] = useState("2026-06-05");
  const [people, setPeople] = useState(2);
  const [message, setMessage] = useState("");
  const [isProcessingQueue, setIsProcessingQueue] = useState(false);

  const [selectedHotel, setSelectedHotel] = useState(null);
  const [lastBooking, setLastBooking] = useState(null);

  const [myBookings, setMyBookings] = useState([]);
  const [showMyBookings, setShowMyBookings] = useState(false);

  const [comments, setComments] = useState({});
  const [commentStats, setCommentStats] = useState({});
  const [commentForms, setCommentForms] = useState({});

  const [aiPrompt, setAiPrompt] = useState("");
  const [aiMessages, setAiMessages] = useState([
    { sender: "You", text: "I want a hotel in Bodrum." },
    {
      sender: "AI",
      text: "I can recommend hotels by rating, price, pool, Wi-Fi and breakfast.",
    },
  ]);

  const [adminHotelId, setAdminHotelId] = useState("");
  const [adminStartDate, setAdminStartDate] = useState("");
  const [adminEndDate, setAdminEndDate] = useState("");
  const [adminRooms, setAdminRooms] = useState("");

  const emptyHotelForm = {
    name: "",
    city: "",
    address: "",
    description: "",
    rating: "",
    totalRooms: "",
    availableRooms: "",
    pricePerNight: "",
    hasPool: false,
    hasWifi: false,
    hasBreakfast: false,
  };

  const [hotelForm, setHotelForm] = useState(emptyHotelForm);
  const [editingHotelId, setEditingHotelId] = useState("");

  const API_BASE_URL =
  "http://api-gateway-lb-env-env.eba-qwz3nust.eu-north-1.elasticbeanstalk.com";
  const getAuthConfig = () => {
    return {
      headers: {
        Authorization: `Bearer ${session?.access_token}`,
      },
    };
  };

  useEffect(() => {
    supabase.auth.getSession().then(({ data: { session } }) => {
      setSession(session);
    });

    const {
      data: { subscription },
    } = supabase.auth.onAuthStateChange((_event, session) => {
      setSession(session);
    });

    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    if (session) {
      getAllHotels();
    }
  }, [session]);

  useEffect(() => {
    hotels.forEach((hotel) => {
      loadComments(hotel.id);
      loadCommentStats(hotel.id);
    });
  }, [hotels]);

  const getHotelImage = (hotel) => {
    const cityName = hotel.city.toLowerCase();

    if (cityName.includes("ankara")) return hotel6;
    if (cityName.includes("istanbul")) return hotel5;
    if (cityName.includes("antalya")) return hotel4;
    if (cityName.includes("izmir")) return hotel3;
    if (cityName.includes("bodrum")) return hotel1;

    return hotel2;
  };

  const calculateNights = () => {
    return (new Date(endDate) - new Date(startDate)) / (1000 * 60 * 60 * 24);
  };

  const getDiscountedPrice = (price) => {
    return Math.round(Number(price) * 0.85);
  };

  const getBookingTotal = (hotel) => {
    return (
      getDiscountedPrice(hotel.pricePerNight) *
      calculateNights() *
      Number(people)
    );
  };

  const getAllHotels = async (showMessage = true) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/v1/hotels`);
      setHotels(response.data);

      if (showMessage) {
        setMessage("All hotels loaded.");
      }

      return response.data;
    } catch (error) {
      console.error(error);
      setMessage("Hotels could not be loaded.");
      return [];
    }
  };

  const searchHotels = () => {
    if (!city || !startDate || !endDate || !people) {
      setMessage("Please fill all search fields.");
      return;
    }

    axios
      .get(`${API_BASE_URL}/api/v1/hotels/search/available`, {
        params: { city, startDate, endDate, people },
      })
      .then((response) => {
        setHotels(response.data);
        setShowMyBookings(false);

        if (response.data.length === 0) {
          setMessage(`No hotels found for ${city}.`);
        } else {
          setMessage(`${response.data.length} hotel(s) found in ${city}.`);
        }
      })
      .catch((error) => {
        console.error(error);
        setMessage("Search failed.");
      });
  };

  const loadComments = async (hotelId) => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/api/v1/comments/hotel/${hotelId}`
      );

      setComments((prev) => ({
        ...prev,
        [hotelId]: response.data,
      }));
    } catch (error) {
      console.error("COMMENT ERROR:", error);
    }
  };

  const loadCommentStats = async (hotelId) => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/api/v1/comments/hotel/${hotelId}/stats`
      );

      setCommentStats((prev) => ({
        ...prev,
        [hotelId]: response.data,
      }));
    } catch (error) {
      console.error("STATS ERROR:", error);
    }
  };

  const handleCommentFormChange = (hotelId, field, value) => {
    setCommentForms((prev) => ({
      ...prev,
      [hotelId]: {
        ...prev[hotelId],
        [field]: value,
      },
    }));
  };

  const addComment = async (hotelId) => {
    const form = commentForms[hotelId] || {};

    if (!form.username || !form.comment || !form.rating) {
      setMessage("Please fill username, rating and comment.");
      return;
    }

    const newComment = {
      hotelId,
      username: form.username,
      comment: form.comment,
      rating: Number(form.rating),
    };

    try {
      await axios.post(`${API_BASE_URL}/api/v1/comments`, newComment);

      setMessage("Comment added successfully.");

      setCommentForms((prev) => ({
        ...prev,
        [hotelId]: {
          username: "",
          comment: "",
          rating: "",
        },
      }));

      loadComments(hotelId);
      loadCommentStats(hotelId);
    } catch (error) {
      console.error("ADD COMMENT ERROR:", error.response?.data || error);
      setMessage("Comment could not be added.");
    }
  };

  const sendAiMessage = async () => {
    if (!aiPrompt.trim()) return;

    const userMessage = aiPrompt;

    setAiMessages((prev) => [...prev, { sender: "You", text: userMessage }]);
    setAiPrompt("");

    try {
      const response = await axios.post(`${API_BASE_URL}/api/v1/ai/chat`, {
        prompt: userMessage,
        city: city || "Bodrum",
        startDate,
        endDate,
        people: String(people),
      });

      setAiMessages((prev) => [
        ...prev,
        { sender: "AI", text: response.data.response },
      ]);
    } catch (error) {
      console.error("AI ERROR:", error.response?.data || error);

      setAiMessages((prev) => [
        ...prev,
        {
          sender: "AI",
          text: "AI service could not respond. Please check ai-agent-service and Ollama.",
        },
      ]);
    }
  };

  const updateAvailability = async () => {
    if (!adminHotelId || !adminStartDate || !adminEndDate || !adminRooms) {
      setMessage("Please fill all admin availability fields.");
      return;
    }

    try {
      await axios.post(
        `${API_BASE_URL}/api/v1/availability`,
        {
          hotelId: Number(adminHotelId),
          startDate: adminStartDate,
          endDate: adminEndDate,
          availableRooms: Number(adminRooms),
        },
        getAuthConfig()
      );

      setMessage("Availability updated successfully. Search the same date range to see the updated date-based room count.");

      setAdminHotelId("");
      setAdminStartDate("");
      setAdminEndDate("");
      setAdminRooms("");

      await getAllHotels(false);
      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (error) {
      console.error("AVAILABILITY ERROR:", error.response?.data || error);
      setMessage(error.response?.data?.message || "Availability update failed.");
    }
  };

  const handleHotelFormChange = (field, value) => {
    setHotelForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const resetHotelForm = () => {
    setHotelForm(emptyHotelForm);
    setEditingHotelId("");
  };

  const getHotelPayload = () => {
    return {
      name: hotelForm.name,
      city: hotelForm.city,
      address: hotelForm.address,
      description: hotelForm.description,
      rating: Number(hotelForm.rating),
      totalRooms: Number(hotelForm.totalRooms),
      availableRooms: Number(hotelForm.availableRooms),
      pricePerNight: Number(hotelForm.pricePerNight),
      hasPool: hotelForm.hasPool,
      hasWifi: hotelForm.hasWifi,
      hasBreakfast: hotelForm.hasBreakfast,
    };
  };

  const createHotel = async () => {
    if (
      !hotelForm.name ||
      !hotelForm.city ||
      !hotelForm.address ||
      !hotelForm.rating ||
      !hotelForm.totalRooms ||
      !hotelForm.availableRooms ||
      !hotelForm.pricePerNight
    ) {
      setMessage("Please fill required hotel fields.");
      return;
    }

    try {
      await axios.post(
        `${API_BASE_URL}/api/v1/hotels`,
        getHotelPayload(),
        getAuthConfig()
      );

      setMessage("Hotel created successfully. New hotel is now visible in the hotel list.");
      resetHotelForm();
      await getAllHotels(false);
      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (error) {
      console.error("CREATE HOTEL ERROR:", error.response?.data || error);
      setMessage(error.response?.data?.message || "Hotel creation failed.");
    }
  };

  const selectHotelForEdit = (hotel) => {
    setEditingHotelId(hotel.id);

    setHotelForm({
      name: hotel.name || "",
      city: hotel.city || "",
      address: hotel.address || "",
      description: hotel.description || "",
      rating: hotel.rating || "",
      totalRooms: hotel.totalRooms || "",
      availableRooms: hotel.availableRooms || "",
      pricePerNight: hotel.pricePerNight || "",
      hasPool: Boolean(hotel.hasPool),
      hasWifi: Boolean(hotel.hasWifi),
      hasBreakfast: Boolean(hotel.hasBreakfast),
    });

    setMessage(`Editing hotel #${hotel.id}`);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const updateHotel = async () => {
    if (!editingHotelId) {
      setMessage("Please select a hotel to update.");
      return;
    }

    try {
      await axios.put(
        `${API_BASE_URL}/api/v1/hotels/${editingHotelId}`,
        getHotelPayload(),
        getAuthConfig()
      );

      setMessage("Hotel updated successfully. Updated values are now visible in the hotel list.");
      resetHotelForm();
      await getAllHotels(false);
      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (error) {
      console.error("UPDATE HOTEL ERROR:", error.response?.data || error);
      setMessage(error.response?.data?.message || "Hotel update failed.");
    }
  };

  const deleteHotel = async (hotelId) => {
    const confirmed = window.confirm(`Are you sure you want to delete hotel #${hotelId}?`);

    if (!confirmed) return;

    try {
      await axios.delete(
        `${API_BASE_URL}/api/v1/hotels/${hotelId}`,
        getAuthConfig()
      );

      setMessage(`Hotel #${hotelId} deleted successfully.`);
      await getAllHotels(false);
      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (error) {
      console.error("DELETE HOTEL ERROR:", error.response?.data || error);
      setMessage(error.response?.data?.message || "Hotel deletion failed.");
    }
  };

  const openHotelDetail = (hotel) => {
    setSelectedHotel(hotel);
    setLastBooking(null);
    setShowMyBookings(false);
    setMessage("");
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const confirmBooking = async () => {
    if (!selectedHotel) return;

    const nights = calculateNights();

    if (nights <= 0) {
      setMessage("Check-out date must be after check-in date.");
      return;
    }

    const bookingRequest = {
      hotelId: selectedHotel.id,
      guestName: session?.user?.email || "Guest",
      startDate,
      endDate,
      peopleCount: Number(people),
      totalPrice: getBookingTotal(selectedHotel),
    };

    try {
      const response = await axios.post(
        `${API_BASE_URL}/api/v1/bookings`,
        bookingRequest,
        getAuthConfig()
      );

      setLastBooking(response.data);
      setMessage("");

      setSelectedHotel((prev) =>
        prev
          ? {
              ...prev,
              availableRooms: prev.availableRooms - Number(people),
            }
          : prev
      );

      await getAllHotels(false);
      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (error) {
      console.error("BOOKING ERROR:", error.response?.data || error);
      setMessage(error.response?.data?.message || "Booking failed");
    }
  };

  const loadMyBookings = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/api/v1/bookings`,
        getAuthConfig()
      );

      const userBookings = response.data.filter(
        (booking) => booking.guestName === session?.user?.email
      );

      setMyBookings(userBookings);
      setShowMyBookings(true);
      setSelectedHotel(null);
      setLastBooking(null);
      setMessage("");
      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (error) {
      console.error("MY BOOKINGS ERROR:", error.response?.data || error);
      setMessage("Bookings could not be loaded.");
    }
  };

  const cancelBooking = async (bookingId) => {
    const confirmed = window.confirm(
      `Are you sure you want to cancel booking #${bookingId}?`
    );

    if (!confirmed) return;

    try {
      await axios.delete(
        `${API_BASE_URL}/api/v1/bookings/${bookingId}`,
        getAuthConfig()
      );

      setMessage(`Booking #${bookingId} cancelled successfully.`);

      await loadMyBookings();
      getAllHotels();
    } catch (error) {
      console.error("CANCEL BOOKING ERROR:", error.response?.data || error);
      setMessage(error.response?.data?.message || "Booking cancellation failed.");
    }
  };

  const processReservationQueue = async () => {
    if (!isAdmin) {
      setMessage("Only admins can process reservation notifications.");
      return;
    }

    try {
      setIsProcessingQueue(true);

      const response = await axios.get(
        `${API_BASE_URL}/api/v1/notifications/process-reservations`,
        getAuthConfig()
      );

      const processedCount = Array.isArray(response.data)
        ? response.data.length
        : 0;

      setMessage(
        processedCount === 0
          ? "No pending reservation notifications found."
          : `${processedCount} reservation notification(s) processed successfully.`
      );

      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (error) {
      console.error("PROCESS QUEUE ERROR:", error.response?.data || error);
      setMessage("Reservation queue could not be processed.");
    } finally {
      setIsProcessingQueue(false);
    }
  };

  if (!session) {
    return <Login />;
  }

  return (
    <div className="app">
      <header className="hero-section">
        <div style={{ textAlign: "right" }}>
          <span style={{ marginRight: "15px", color: "#1e3a8a" }}>
            {isAdmin ? "Admin" : "Customer"}: {session.user.email}
          </span>

          {!isAdmin && (
            <button
              onClick={loadMyBookings}
              style={{
                background: "#16a34a",
                color: "white",
                border: "none",
                padding: "10px 16px",
                borderRadius: "10px",
                cursor: "pointer",
                fontWeight: "bold",
                marginRight: "10px",
              }}
            >
              My Reservations
            </button>
          )}

          {isAdmin && (
            <button
              onClick={processReservationQueue}
              disabled={isProcessingQueue}
              style={{
                background: "#7c3aed",
                color: "white",
                border: "none",
                padding: "10px 16px",
                borderRadius: "10px",
                cursor: "pointer",
                fontWeight: "bold",
                marginRight: "10px",
                opacity: isProcessingQueue ? 0.7 : 1,
              }}
            >
              {isProcessingQueue ? "Processing..." : "Process Queue"}
            </button>
          )}

          <button
            onClick={async () => {
              await supabase.auth.signOut();
            }}
            style={{
              background: "#dc2626",
              color: "white",
              border: "none",
              padding: "10px 16px",
              borderRadius: "10px",
              cursor: "pointer",
              fontWeight: "bold",
            }}
          >
            Logout
          </button>
        </div>

        <div className="brand-row">
          <span className="brand-logo">🏨</span>
          <span className="brand-name">
            {isAdmin ? "Hotel Admin Dashboard" : "Hotel Booking System"}
          </span>
        </div>

        <h1>{isAdmin ? "Manage hotels and availability" : "Find your next stay"}</h1>

        <p className="hero-subtitle">
          {isAdmin
            ? "Create, update and delete hotels. Manage room availability between selected dates."
            : "Search hotels by destination, dates and number of people. Logged-in customers see 15% discounted prices."}
        </p>

        <div className="search-bar">
          <div className="input-group">
            <label>Destination</label>
            <input
              type="text"
              placeholder="Where are you going?"
              value={city}
              onChange={(e) => setCity(e.target.value)}
            />
          </div>

          <div className="input-group">
            <label>Check In</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
            />
          </div>

          <div className="input-group">
            <label>Check Out</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
            />
          </div>

          <div className="input-group">
            <label>Guests</label>
            <input
              type="number"
              min="1"
              value={people}
              onChange={(e) => setPeople(e.target.value)}
            />
          </div>

          <button onClick={searchHotels}>Search</button>

          <button className="secondary-btn" onClick={getAllHotels}>
            All Hotels
          </button>
        </div>

        {message && <p className="status-message">{message}</p>}
      </header>

      {isAdmin && (
        <section
          style={{
            background: "white",
            margin: "30px auto",
            padding: "30px",
            borderRadius: "24px",
            maxWidth: "1050px",
            boxShadow: "0 12px 35px rgba(0,0,0,0.12)",
          }}
        >
          <h2 style={{ color: "#1e3a8a", fontSize: "30px", marginBottom: "20px" }}>
            Admin Hotel Management
          </h2>

          <p style={{ color: "#475569", marginBottom: "8px" }}>
            {editingHotelId
              ? `Editing Hotel ID: ${editingHotelId}`
              : "Create a new hotel or select an existing hotel to update."}
          </p>

          <p style={{ color: "#64748b", marginBottom: "20px", fontSize: "14px" }}>
            General available rooms are stored on the hotel record. Date-based availability is managed from the Admin Availability panel.
          </p>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(3, 1fr)",
              gap: "15px",
            }}
          >
            <input
              type="text"
              placeholder="Hotel name"
              value={hotelForm.name}
              onChange={(e) => handleHotelFormChange("name", e.target.value)}
            />

            <input
              type="text"
              placeholder="City"
              value={hotelForm.city}
              onChange={(e) => handleHotelFormChange("city", e.target.value)}
            />

            <input
              type="text"
              placeholder="Address"
              value={hotelForm.address}
              onChange={(e) => handleHotelFormChange("address", e.target.value)}
            />

            <input
              type="text"
              placeholder="Description"
              value={hotelForm.description}
              onChange={(e) =>
                handleHotelFormChange("description", e.target.value)
              }
            />

            <input
              type="number"
              step="0.1"
              min="0"
              max="5"
              placeholder="Rating 0-5"
              value={hotelForm.rating}
              onChange={(e) => handleHotelFormChange("rating", e.target.value)}
            />

            <input
              type="number"
              min="1"
              placeholder="Total rooms"
              value={hotelForm.totalRooms}
              onChange={(e) =>
                handleHotelFormChange("totalRooms", e.target.value)
              }
            />

            <input
              type="number"
              min="0"
              placeholder="General available rooms"
              value={hotelForm.availableRooms}
              onChange={(e) =>
                handleHotelFormChange("availableRooms", e.target.value)
              }
            />

            <input
              type="number"
              min="1"
              placeholder="Price per night"
              value={hotelForm.pricePerNight}
              onChange={(e) =>
                handleHotelFormChange("pricePerNight", e.target.value)
              }
            />

            <select
              value={editingHotelId}
              onChange={(e) => {
                const selected = hotels.find(
                  (hotel) => String(hotel.id) === e.target.value
                );

                if (selected) {
                  selectHotelForEdit(selected);
                } else {
                  resetHotelForm();
                }
              }}
            >
              <option value="">Select hotel to edit</option>
              {hotels.map((hotel) => (
                <option key={hotel.id} value={hotel.id}>
                  #{hotel.id} - {hotel.name}
                </option>
              ))}
            </select>
          </div>

          <div
            style={{
              display: "flex",
              gap: "20px",
              marginTop: "20px",
              flexWrap: "wrap",
              color: "#1e3a8a",
              fontWeight: "bold",
            }}
          >
            <label>
              <input
                type="checkbox"
                checked={hotelForm.hasPool}
                onChange={(e) =>
                  handleHotelFormChange("hasPool", e.target.checked)
                }
              />{" "}
              Pool
            </label>

            <label>
              <input
                type="checkbox"
                checked={hotelForm.hasWifi}
                onChange={(e) =>
                  handleHotelFormChange("hasWifi", e.target.checked)
                }
              />{" "}
              Wi-Fi
            </label>

            <label>
              <input
                type="checkbox"
                checked={hotelForm.hasBreakfast}
                onChange={(e) =>
                  handleHotelFormChange("hasBreakfast", e.target.checked)
                }
              />{" "}
              Breakfast
            </label>
          </div>

          <div
            style={{
              display: "flex",
              gap: "12px",
              marginTop: "25px",
              flexWrap: "wrap",
            }}
          >
            <button
              onClick={createHotel}
              style={{
                background: "#16a34a",
                color: "white",
                border: "none",
                padding: "12px 18px",
                borderRadius: "12px",
                cursor: "pointer",
                fontWeight: "bold",
              }}
            >
              Add Hotel
            </button>

            <button
              onClick={updateHotel}
              style={{
                background: "#2563eb",
                color: "white",
                border: "none",
                padding: "12px 18px",
                borderRadius: "12px",
                cursor: "pointer",
                fontWeight: "bold",
              }}
            >
              Update Selected Hotel
            </button>

            <button
              onClick={resetHotelForm}
              style={{
                background: "#64748b",
                color: "white",
                border: "none",
                padding: "12px 18px",
                borderRadius: "12px",
                cursor: "pointer",
                fontWeight: "bold",
              }}
            >
              Clear Form
            </button>
          </div>
        </section>
      )}

      {showMyBookings && (
        <section
          style={{
            background: "white",
            margin: "30px auto",
            padding: "30px",
            borderRadius: "24px",
            maxWidth: "1050px",
            boxShadow: "0 12px 35px rgba(0,0,0,0.12)",
          }}
        >
          <button
            onClick={() => setShowMyBookings(false)}
            style={{
              background: "transparent",
              border: "1px solid #2563eb",
              color: "#2563eb",
              padding: "10px 18px",
              borderRadius: "12px",
              cursor: "pointer",
              fontWeight: "bold",
              marginBottom: "20px",
            }}
          >
            ← Back to hotels
          </button>

          <h2 style={{ color: "#1e3a8a", fontSize: "32px" }}>
            My Reservations
          </h2>

          {myBookings.length === 0 ? (
            <p>No reservations found.</p>
          ) : (
            myBookings.map((booking) => (
              <div
                key={booking.id}
                style={{
                  background: "#f8fafc",
                  padding: "20px",
                  borderRadius: "16px",
                  marginTop: "16px",
                  border: "1px solid #e5e7eb",
                }}
              >
                <h3 style={{ color: "#1e3a8a" }}>Booking #{booking.id}</h3>
                <p>
                  <strong>Hotel ID:</strong> {booking.hotelId}
                </p>
                <p>
                  <strong>Guest:</strong> {booking.guestName}
                </p>
                <p>
                  <strong>Check-in:</strong> {booking.startDate}
                </p>
                <p>
                  <strong>Check-out:</strong> {booking.endDate}
                </p>
                <p>
                  <strong>Guests:</strong> {booking.peopleCount}
                </p>
                <p>
                  <strong>Total Price:</strong> {booking.totalPrice} TL
                </p>
                <p>
                  <strong>Status:</strong> {booking.status}
                </p>

                <button
                  onClick={() => cancelBooking(booking.id)}
                  style={{
                    background: "#dc2626",
                    color: "white",
                    border: "none",
                    padding: "10px 16px",
                    borderRadius: "10px",
                    cursor: "pointer",
                    fontWeight: "bold",
                    marginTop: "10px",
                  }}
                >
                  Cancel Reservation
                </button>
              </div>
            ))
          )}
        </section>
      )}

      {selectedHotel && (
        <section
          style={{
            background: "white",
            margin: "30px auto",
            padding: "30px",
            borderRadius: "24px",
            maxWidth: "1050px",
            boxShadow: "0 12px 35px rgba(0,0,0,0.12)",
          }}
        >
          <button
            onClick={() => {
              setSelectedHotel(null);
              setLastBooking(null);
            }}
            style={{
              background: "transparent",
              border: "1px solid #2563eb",
              color: "#2563eb",
              padding: "10px 18px",
              borderRadius: "12px",
              cursor: "pointer",
              fontWeight: "bold",
              marginBottom: "20px",
            }}
          >
            ← Back to hotels
          </button>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: "30px",
            }}
          >
            <img
              src={getHotelImage(selectedHotel)}
              alt="selected hotel"
              style={{
                width: "100%",
                borderRadius: "20px",
                maxHeight: "460px",
                objectFit: "cover",
              }}
            />

            <div>
              <h2 style={{ color: "#1e3a8a", fontSize: "34px" }}>
                {selectedHotel.name}
              </h2>

              <p>
                📍 {selectedHotel.city} - {selectedHotel.address}
              </p>

              <p>
                <strong>Description:</strong> {selectedHotel.description}
              </p>

              <div className="amenities">
                {selectedHotel.hasPool && <span>🏊 Pool</span>}
                {selectedHotel.hasWifi && <span>📶 Wi-Fi</span>}
                {selectedHotel.hasBreakfast && <span>🍳 Breakfast</span>}
              </div>

              <div
                style={{
                  marginTop: "25px",
                  background: "#f8fafc",
                  padding: "24px",
                  borderRadius: "18px",
                }}
              >
                <h3 style={{ color: "#1e3a8a" }}>Reservation Details</h3>

                <p>
                  <strong>Guest:</strong> {session?.user?.email}
                </p>
                <p>
                  <strong>Check-in:</strong> {startDate}
                </p>
                <p>
                  <strong>Check-out:</strong> {endDate}
                </p>
                <p>
                  <strong>Guests:</strong> {people}
                </p>
                <p>
                  <strong>Date-based Available Rooms:</strong>{" "}
                  {selectedHotel.availableRooms}
                </p>

                <p>
                  <strong>Price per night:</strong>{" "}
                  <span style={{ textDecoration: "line-through", color: "#64748b" }}>
                    {selectedHotel.pricePerNight} TL
                  </span>{" "}
                  <span style={{ color: "#16a34a", fontWeight: "bold" }}>
                    {getDiscountedPrice(selectedHotel.pricePerNight)} TL
                  </span>
                  <br />
                  <small style={{ color: "#16a34a" }}>
                    15% member discount applied
                  </small>
                </p>

                <h3>Total: {getBookingTotal(selectedHotel)} TL</h3>

                {!lastBooking ? (
                  <button
                    onClick={confirmBooking}
                    style={{
                      width: "100%",
                      padding: "15px",
                      background: "#2563eb",
                      color: "white",
                      border: "none",
                      borderRadius: "14px",
                      fontWeight: "bold",
                      fontSize: "16px",
                      cursor: "pointer",
                    }}
                  >
                    Confirm Reservation
                  </button>
                ) : (
                  <div
                    style={{
                      marginTop: "20px",
                      background: "#dcfce7",
                      color: "#166534",
                      padding: "20px",
                      borderRadius: "16px",
                      fontWeight: "bold",
                    }}
                  >
                    ✅ Reservation confirmed!
                    <br />
                    Booking ID: {lastBooking.id}
                    <br />
                    Status: {lastBooking.status}
                    <br />
                    Total Price: {lastBooking.totalPrice} TL
                  </div>
                )}
              </div>
            </div>
          </div>
        </section>
      )}

      <main className="main-layout">
        <aside className="left-sidebar">
          <div className="map-section">
            <h3>Map View</h3>

            <div className="fake-map">
              <div className="map-pin">📍</div>
              <p>Hotels searched in selected destination</p>

              <button
                className="map-btn"
                onClick={() => {
                  const location = city || "Bodrum";
                  window.open(
                    `https://www.google.com/maps/search/hotels+in+${location}`,
                    "_blank"
                  );
                }}
              >
                Show on Map
              </button>
            </div>
          </div>

          <div className="ai-chat">
            <h4>AI Hotel Assistant</h4>

            <div className="chat-box">
              {aiMessages.map((msg, index) => (
                <p key={index}>
                  <strong>{msg.sender}:</strong> {msg.text}
                </p>
              ))}
            </div>

            <input
              type="text"
              placeholder="Ask AI assistant..."
              value={aiPrompt}
              onChange={(e) => setAiPrompt(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") sendAiMessage();
              }}
            />

            <button className="chat-btn" onClick={sendAiMessage}>
              Send
            </button>
          </div>

          {isAdmin && (
            <div className="admin-box">
              <h4>Admin Availability</h4>

              <p>Update date-based room availability between selected dates.</p>

              <select
                value={adminHotelId}
                onChange={(e) => setAdminHotelId(e.target.value)}
              >
                <option value="">Select hotel</option>
                {hotels.map((hotel) => (
                  <option key={hotel.id} value={hotel.id}>
                    #{hotel.id} - {hotel.name}
                  </option>
                ))}
              </select>

              <input
                type="date"
                value={adminStartDate}
                onChange={(e) => setAdminStartDate(e.target.value)}
              />

              <input
                type="date"
                value={adminEndDate}
                onChange={(e) => setAdminEndDate(e.target.value)}
              />

              <input
                type="number"
                placeholder="Room count"
                value={adminRooms}
                onChange={(e) => setAdminRooms(e.target.value)}
              />

              <button onClick={updateAvailability}>Update Rooms</button>
            </div>
          )}
        </aside>

        <section className="hotel-list">
          {hotels.map((hotel) => (
            <div className="hotel-card" key={hotel.id}>
              <img
                src={getHotelImage(hotel)}
                alt="hotel"
                className="hotel-image"
              />

              <div className="hotel-info">
                <div className="hotel-top-row">
                  <div>
                    <h2>{hotel.name}</h2>
                    <p className="hotel-city">📍 {hotel.city}</p>
                  </div>

                  <div className="rating-badge">⭐ {hotel.rating}</div>
                </div>

                <p>
                  <strong>Address:</strong> {hotel.address}
                </p>

                <p>
                  <strong>Description:</strong> {hotel.description}
                </p>

                <div className="amenities">
                  {hotel.hasPool && <span>🏊 Pool</span>}
                  {hotel.hasWifi && <span>📶 Wi-Fi</span>}
                  {hotel.hasBreakfast && <span>🍳 Breakfast</span>}
                </div>

                <div className="price-section">
                  <div>
                    {!isAdmin ? (
                      <>
                        <p
                          style={{
                            textDecoration: "line-through",
                            color: "#64748b",
                            margin: 0,
                          }}
                        >
                          {hotel.pricePerNight} TL
                        </p>

                        <h3 style={{ color: "#16a34a", margin: 0 }}>
                          {getDiscountedPrice(hotel.pricePerNight)} TL
                        </h3>

                        <p>per night with 15% member discount</p>
                      </>
                    ) : (
                      <>
                        <h3>{hotel.pricePerNight} TL</h3>
                        <p>per night</p>
                      </>
                    )}
                  </div>

                  <div>
                    <p>
                      <strong>
                        {isAdmin ? "General Available Rooms:" : "Date-based Available Rooms:"}
                      </strong>{" "}
                      {hotel.availableRooms}
                    </p>
                  </div>
                </div>

                {isAdmin && (
                  <div style={{ display: "flex", gap: "10px", marginBottom: "15px" }}>
                    <button
                      className="book-btn"
                      onClick={() => selectHotelForEdit(hotel)}
                    >
                      Edit Hotel
                    </button>

                    <button
                      onClick={() => deleteHotel(hotel.id)}
                      style={{
                        background: "#dc2626",
                        color: "white",
                        border: "none",
                        padding: "12px 18px",
                        borderRadius: "12px",
                        cursor: "pointer",
                        fontWeight: "bold",
                      }}
                    >
                      Delete Hotel
                    </button>
                  </div>
                )}

                {!isAdmin && (
                  <button
                    className="book-btn"
                    onClick={() => openHotelDetail(hotel)}
                  >
                    Book Hotel
                  </button>
                )}

                <div className="hotel-comments">
                  <h4>Comments & Ratings</h4>

                  <div className="comment-bars">
                    <div className="comment-row">
                      <span>Average</span>
                      <div className="bar">
                        <div className="bar-fill wide"></div>
                      </div>
                      <strong>
                        {commentStats[hotel.id]?.averageRating?.toFixed(1) ||
                          "0.0"}
                      </strong>
                    </div>

                    <div className="comment-row">
                      <span>Total</span>
                      <div className="bar">
                        <div className="bar-fill medium"></div>
                      </div>
                      <strong>
                        {commentStats[hotel.id]?.totalComments || 0}
                      </strong>
                    </div>
                  </div>

                  {(comments[hotel.id] || []).length === 0 && (
                    <p>No comments yet.</p>
                  )}

                  {(comments[hotel.id] || []).map((comment) => (
                    <div className="comment-card" key={comment.id}>
                      <h5>{comment.username}</h5>
                      <p className="comment-rating">⭐ {comment.rating} / 10</p>
                      <p>“{comment.comment}”</p>
                    </div>
                  ))}

                  {!isAdmin && (
                    <div className="comment-card">
                      <h5>Add Comment</h5>

                      <input
                        type="text"
                        placeholder="Your name"
                        value={commentForms[hotel.id]?.username || ""}
                        onChange={(e) =>
                          handleCommentFormChange(
                            hotel.id,
                            "username",
                            e.target.value
                          )
                        }
                      />

                      <input
                        type="number"
                        min="1"
                        max="10"
                        placeholder="Rating 1-10"
                        value={commentForms[hotel.id]?.rating || ""}
                        onChange={(e) =>
                          handleCommentFormChange(
                            hotel.id,
                            "rating",
                            e.target.value
                          )
                        }
                      />

                      <input
                        type="text"
                        placeholder="Write your comment"
                        value={commentForms[hotel.id]?.comment || ""}
                        onChange={(e) =>
                          handleCommentFormChange(
                            hotel.id,
                            "comment",
                            e.target.value
                          )
                        }
                      />

                      <button
                        className="book-btn"
                        onClick={() => addComment(hotel.id)}
                      >
                        Add Comment
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </section>
      </main>
    </div>
  );
}

export default App;