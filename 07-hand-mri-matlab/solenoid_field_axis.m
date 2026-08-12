function Bz = solenoid_field_axis(z, R, L, N, I)
%SOLENOID_FIELD_AXIS Magnetic field along the axis of a finite solenoid.
%
% Inputs
%   z : axial position(s) measured from solenoid center [m]
%   R : solenoid radius [m]
%   L : solenoid length [m]
%   N : number of turns
%   I : current [A]
%
% Output
%   Bz : axial magnetic field [T]

mu0 = 4*pi*1e-7;
n = N/L;

term1 = (z + L/2) ./ sqrt(R^2 + (z + L/2).^2);
term2 = (z - L/2) ./ sqrt(R^2 + (z - L/2).^2);

Bz = (mu0*n*I/2).*(term1-term2);
end
