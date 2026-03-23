import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiService } from '../services/apiService';
import Loading from '../components/Loading';

function Profile() {
  const { user, refreshUser } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const [newUsername, setNewUsername] = useState('');
  const [newAvatarUrl, setNewAvatarUrl] = useState('');

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const data = await apiService.getCurrentUser();
      setProfile(data);
      setNewUsername(data.username);
      setNewAvatarUrl(data.avatarUrl || '');
    } catch (error) {
      setError(error.message || 'Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const updatedProfile = await apiService.updateProfile({
        username: newUsername !== profile?.username ? newUsername : undefined,
        avatarUrl: newAvatarUrl !== profile?.avatarUrl ? newAvatarUrl : undefined,
      });
      setProfile(updatedProfile);
      setEditing(false);
      setSuccess('Profile updated successfully!');
      await refreshUser();
    } catch (error) {
      setError(error.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setNewUsername(profile?.username || '');
    setNewAvatarUrl(profile?.avatarUrl || '');
    setEditing(false);
    setError(null);
  };

  if (loading) {
    return (
      <div className="container mt-5">
        <Loading message="Loading profile..." />
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger">Failed to load profile</div>
      </div>
    );
  }

  const winRate =
    profile.wins + profile.losses > 0
      ? ((profile.wins / (profile.wins + profile.losses)) * 100).toFixed(1)
      : '0.0';

  return (
    <div className="container mt-4">
      <h2 className="mb-4">👤 Profile</h2>

      {error && (
        <div className="alert alert-danger alert-dismissible">
          {error}
          <button
            type="button"
            className="btn-close"
            onClick={() => setError(null)}
          ></button>
        </div>
      )}

      {success && (
        <div className="alert alert-success alert-dismissible">
          {success}
          <button
            type="button"
            className="btn-close"
            onClick={() => setSuccess(null)}
          ></button>
        </div>
      )}

      <div className="row">
        {/* Profile Card */}
        <div className="col-md-4 mb-4">
          <div className="card">
            <div className="card-body text-center">
              <div
                className="rounded-circle bg-secondary d-inline-flex align-items-center justify-content-center mb-3"
                style={{ width: '100px', height: '100px', fontSize: '40px' }}
              >
                {profile.avatarUrl ? (
                  <img
                    src={profile.avatarUrl}
                    alt="Avatar"
                    className="rounded-circle"
                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  />
                ) : (
                  '👤'
                )}
              </div>

              {editing ? (
                <div>
                  <div className="mb-3">
                    <label className="form-label">Username</label>
                    <input
                      type="text"
                      className="form-control"
                      value={newUsername}
                      onChange={(e) => setNewUsername(e.target.value)}
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Avatar URL</label>
                    <input
                      type="text"
                      className="form-control"
                      value={newAvatarUrl}
                      onChange={(e) => setNewAvatarUrl(e.target.value)}
                      placeholder="https://..."
                    />
                  </div>
                  <div className="d-flex gap-2">
                    <button
                      className="btn btn-primary flex-grow-1"
                      onClick={handleSave}
                      disabled={saving}
                    >
                      {saving ? 'Saving...' : 'Save'}
                    </button>
                    <button
                      className="btn btn-outline-secondary flex-grow-1"
                      onClick={handleCancel}
                      disabled={saving}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <div>
                  <h4>{profile.username}</h4>
                  <p className="text-muted mb-3">{profile.email}</p>
                  <span
                    className={`badge fs-6 ${
                      profile.rankTitle === 'Grandmaster'
                        ? 'bg-danger'
                        : profile.rankTitle === 'Master'
                        ? 'bg-warning text-dark'
                        : profile.rankTitle === 'Expert'
                        ? 'bg-primary'
                        : 'bg-secondary'
                    }`}
                  >
                    {profile.rankTitle}
                  </span>
                  <button
                    className="btn btn-outline-primary btn-sm mt-3 d-block mx-auto"
                    onClick={() => setEditing(true)}
                  >
                    Edit Profile
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Stats Card */}
        <div className="col-md-8 mb-4">
          <div className="card">
            <div className="card-header">
              <h5 className="mb-0">📊 Statistics</h5>
            </div>
            <div className="card-body">
              <div className="row text-center">
                <div className="col-md-3 col-6 mb-3">
                  <h2 className="text-primary">{profile.rating}</h2>
                  <p className="text-muted mb-0">Rating</p>
                </div>
                <div className="col-md-3 col-6 mb-3">
                  <h2 className="text-success">{profile.wins}</h2>
                  <p className="text-muted mb-0">Wins</p>
                </div>
                <div className="col-md-3 col-6 mb-3">
                  <h2 className="text-danger">{profile.losses}</h2>
                  <p className="text-muted mb-0">Losses</p>
                </div>
                <div className="col-md-3 col-6 mb-3">
                  <h2>{winRate}%</h2>
                  <p className="text-muted mb-0">Win Rate</p>
                </div>
              </div>

              {profile.statistics && (
                <>
                  <hr />
                  <div className="row text-center">
                    <div className="col-md-3 col-6 mb-3">
                      <h4>{profile.statistics.totalBattles}</h4>
                      <p className="text-muted mb-0 small">Total Battles</p>
                    </div>
                    <div className="col-md-3 col-6 mb-3">
                      <h4>{profile.statistics.problemsSolved}</h4>
                      <p className="text-muted mb-0 small">Problems Solved</p>
                    </div>
                    <div className="col-md-3 col-6 mb-3">
                      <h4>{profile.statistics.currentStreak}</h4>
                      <p className="text-muted mb-0 small">Current Streak</p>
                    </div>
                    <div className="col-md-3 col-6 mb-3">
                      <h4>{profile.statistics.maxStreak}</h4>
                      <p className="text-muted mb-0 small">Max Streak</p>
                    </div>
                  </div>

                  <hr />
                  <h6>Problems by Difficulty</h6>
                  <div className="row text-center">
                    <div className="col-4">
                      <span className="badge bg-success">
                        {profile.statistics.easySolved} Easy
                      </span>
                    </div>
                    <div className="col-4">
                      <span className="badge bg-warning text-dark">
                        {profile.statistics.mediumSolved} Medium
                      </span>
                    </div>
                    <div className="col-4">
                      <span className="badge bg-danger">
                        {profile.statistics.hardSolved} Hard
                      </span>
                    </div>
                  </div>
                </>
              )}
            </div>
          </div>

          {/* Account Info */}
          <div className="card mt-4">
            <div className="card-header">
              <h5 className="mb-0">ℹ️ Account Info</h5>
            </div>
            <div className="card-body">
              <p>
                <strong>Member since:</strong>{' '}
                {new Date(profile.createdAt).toLocaleDateString()}
              </p>
              {profile.statistics?.preferredLanguage && (
                <p>
                  <strong>Preferred Language:</strong>{' '}
                  {profile.statistics.preferredLanguage}
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile;